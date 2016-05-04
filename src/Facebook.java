import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import fb.Comment;

public class Facebook {

	int k = 5;
	int minCount = 2;
	List<Comment> fbComments;
	Set<String> stopWords;

	public Facebook(String fbData, String stopWords) throws IOException {

		// read data
		Scanner scn = new Scanner(new File(fbData));
		this.fbComments = new ArrayList<>();
		scn.nextLine(); // get rid of first line
		while (scn.hasNextLine()) {
			Comment comment = new Comment(scn.nextLine());
			cleanComment(comment);
			if (!comment.text.trim().equals(""))
				fbComments.add(comment);
		}
		scn.close();

		// stop words
		scn = new Scanner(new File(stopWords));
		this.stopWords = new HashSet<>();
		while (scn.hasNextLine()) {
			String word = cleanString(scn.nextLine());
			this.stopWords.add(word);
		}

		// build dictionary
		Set<String> dictionary = buildDictionary(fbComments, this.stopWords, minCount);

		// strip comments
		stripComments(fbComments, dictionary);
		
		// find dupes
		findDupes();

		// build comment vectors
		//buildCommentVectors(fbComments, dictionary);

		// declare victory
		System.out.println("Done!");

	}

	private void findDupes() throws IOException {
		// Check for dupes
		Map<String, Integer> counter = new HashMap<>();
		Set<String> dupes = new HashSet<>();
		for (Comment c : fbComments) {
			if (!counter.containsKey(c.text)) {
				counter.put(c.text, 1);
			} else {
				counter.put(c.text, counter.get(c.text) + 1);
			}
		}

		// print out unique comments
		counter = sortMapByValue(counter);
		FileWriter writer = new FileWriter(new File("unique-comments.txt"));
		for (String comment : counter.keySet()) {
			writer.write("Comment: (" + counter.get(comment) + ") : " + comment + "\n");
		}
		writer.close();

		// num uniques
		System.out.println("Num unique comments: " + counter.keySet().size());
	}

	private void buildCommentVectors(List<Comment> comments, Set<String> dictionary) {
		for (Comment c : comments) {
			c.vector = buildCommentVector(c, getZeroDictionary(dictionary));
			// printVector(c.vector);
		}
	}

	private void printVector(double[] v) {
		String[] arr = new String[v.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = "" + v[i];
		}
		System.out.println("[" + String.join(" ", arr) + "]");
	}

	private Map<String, Integer> getZeroDictionary(Set<String> dictionary) {
		Map<String, Integer> zeroDict = new HashMap<>();
		for (String word : dictionary)
			zeroDict.put(word, 0);
		return zeroDict;
	}

	private double[] buildCommentVector(Comment c, Map<String, Integer> zeroDict) {
		for (String s : c.text.split("\\s+")) {
			if (!zeroDict.containsKey(s)) {
				zeroDict.put(s, 1);
			} else {
				zeroDict.put(s, zeroDict.get(s) + 1);
			}
		}
		zeroDict = sortMapByKey(zeroDict);
		double[] vector = new double[zeroDict.size()];
		int i = 0;
		for (String s : zeroDict.keySet())
			vector[i++] = zeroDict.get(s);
		return vector;
	}

	private void stripComments(List<Comment> comments, Set<String> dictionary) {
		for (Comment c : comments) {
			StringBuilder sb = new StringBuilder();
			for (String s : c.text.split("\\s+")) {
				if (dictionary.contains(s)) {
					sb.append(s + " ");
				}
			}
			c.text = sb.toString().trim();
		}
	}

	private Set<String> buildDictionary(List<Comment> comments, Set<String> stopWords, int count) {
		Map<String, Integer> dict = new HashMap<>();
		for (Comment c : comments) {
			for (String s : c.text.split("\\s+")) {
				if (!stopWords.contains(s)) {
					if (!dict.containsKey(s)) {
						dict.put(s, 1);
					} else {
						dict.put(s, dict.get(s) + 1);
					}
				}
			}
		}
		Set<String> dictSet = new HashSet<>();
		for (String s : dict.keySet()) {
			if (dict.get(s) > count) {
				dictSet.add(s);
			}
		}
		return dictSet;
	}

	private Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
		Map<String, Integer> result = new LinkedHashMap<>();
		Stream<Map.Entry<String, Integer>> st = map.entrySet().stream();
		st.sorted(Map.Entry.comparingByValue()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}

	private Map<String, Integer> sortMapByKey(Map<String, Integer> map) {
		Map<String, Integer> result = new LinkedHashMap<>();
		Stream<Map.Entry<String, Integer>> st = map.entrySet().stream();
		st.sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
		return result;
	}

	private void cleanComment(Comment comment) {
		comment.text = cleanString(comment.text);
	}

	private String cleanString(String s) {
		return s.toLowerCase().replaceAll("[^a-z0-9 ]*", "").replaceAll("\\s+", " ").trim();
	}

	public static void main(String[] args) throws IOException {
		Facebook fb = new Facebook("facebook-data.csv", "stop-words.txt");
	}

}
