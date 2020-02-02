package com.example.refreshit;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlComparer {

	//don't care HOW it works, if it WORKS
	//else: https://ru.wikipedia.org/wiki/%D0%A0%D0%B0%D1%81%D1%81%D1%82%D0%BE%D1%8F%D0%BD%D0%B8%D0%B5_%D0%94%D0%B0%D0%BC%D0%B5%D1%80%D0%B0%D1%83_%E2%80%94_%D0%9B%D0%B5%D0%B2%D0%B5%D0%BD%D1%88%D1%82%D0%B5%D0%B9%D0%BD%D0%B0
	static int distance(String a, String b){
		Map<Pair<Integer, Integer>, Integer> d = new HashMap<>();
		int len1 = a.length(), len2 = b.length();
		for (int i = -1; i < len1 + 1; i++) {
			d.put(new Pair<>(i, -1), i+1);
		}
		for (int j = -1; j < len2 + 1; j++) {
			d.put(new Pair<>(-1, j), j+1);
		}

		for (int i = 0; i < len1; i++) {
			for (int j = 0; j < len2; j++) {
				int cost = 1;
				if(a.charAt(i) == b.charAt(j)){
					cost = 0;
				}
				
				int cur = min(d.get(new Pair<>(i-1, j))+1,
							d.get(new Pair<>(i, j-1))+1,
							d.get(new Pair<>(i-1,j-1))+cost);

				if(i!=0 && j!=0 && a.charAt(i)==b.charAt(j-1) && a.charAt(i-1) == b.charAt(j))
					cur = Math.min(cur, d.get(new Pair<>(i-2,j-2)) + cost); //transposition
				
				d.put(new Pair<>(i,j), cur);
			}
		}
		
		return d.get(new Pair<>(len1, len2));
	}

	static int tableCompare(List<String> a, List<String> b){
		return 0;
	}

	static List<String> getContentFromHtml(String html){
		//normalize
		html = html.toLowerCase();
		html = html.replaceAll("[\\s\\t\\n]", "");

		String onlyHtml = "";
		//delete all <script> ... </script>
		for(int i = 0, j = html.indexOf("<script"); i != -1;
		    i = html.indexOf("</script>", j), j = html.indexOf("<script", i)){
			if(j==-1)
				j = html.length();
			onlyHtml += html.substring(i + "</script>".length(), j);
		}

		List<String> result = new ArrayList<>();

		//like >content007<
		Pattern pattern = Pattern.compile(">[^<>]{1,100}</");

		Matcher matcher = pattern.matcher(onlyHtml);
		while (matcher.find()) {
			int start=matcher.start();
			int end=matcher.end();
			result.add(onlyHtml.substring(start+1,end-1));
		}

		return result;
	}

	static String getContentFromHtmlByTag(String html){
		//between that tags we are finding content
		List<String> tags = Arrays.asList("title", "p", "a", "label", "span", "ul", "li", "dl",
				"h1", "h2", "h3", "h4", "h5", "h6");

		//normalize
		html = html.toLowerCase();
		html = html.replaceAll(" ", "");

		String onlyHtml = "";
		//delete all <script> ... </script>
		for(int i = 0, j = html.indexOf("<script"); i != -1;
		    i = html.indexOf("</script>", j), j = html.indexOf("<script", i)){
			if(j==-1)
				j = html.length();
			onlyHtml += html.substring(i + "</script>".length(), j);
		}

		String result = "";

		for (String tag :
				tags) {
			//we are finding "<tag" because it can be like <a href="lalahey">
			for(int start = onlyHtml.indexOf("<"+tag), end = 0; start!=-1 && end!=-1; start = onlyHtml.indexOf("<"+tag, end)){
				end = onlyHtml.indexOf("</"+tag+">", start);
				if(end!=-1)
					result += onlyHtml.substring(start + 1 + tag.length(), end);
			}
		}

		return result;
	}

	private static Integer min(int deletion, int insertion, int substitution) {
		return Math.min(deletion, Math.min(insertion, substitution));
	}
}
