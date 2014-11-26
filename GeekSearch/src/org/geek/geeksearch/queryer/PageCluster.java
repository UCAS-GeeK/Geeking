package org.geek.geeksearch.queryer;

import java.util.ArrayList;
import java.util.List;

import org.geek.geeksearch.model.PageInfo;



/**
 * 使用LCS算法对网页标题计算相似度得分，从而聚类
 * 相似度大于75%则判为相似
 */
public class PageCluster {
	
	/* 网页聚类入口  */
	public static List<List<PageInfo>> doCluster(List<PageInfo> pageList) {
		List<List<PageInfo>> result = new ArrayList<List<PageInfo>>();
		List<PageInfo> cluster;
		PageInfo page, tmp;
		try {
			for (int k = 0; k < pageList.size(); k++) {
				cluster = new ArrayList<PageInfo>();
				page = (PageInfo)pageList.get(k).clone();
				cluster.add(page);
				for (int i = k+1; i < pageList.size(); i++) {
					tmp = (PageInfo)pageList.get(i).clone();
					if (isSimilarPage(page, tmp)) {
						cluster.add(tmp);
						pageList.remove(i--);
					}				
				}
				result.add(cluster);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static boolean isSimilarPage(PageInfo pg1, PageInfo pg2) {
		String title1 = removeSign(pg1.getTitle());
		String title2 = removeSign(pg2.getTitle());
		int maxLen = Math.max(title1.length(), title2.length());
		int subStrLen = longestCommonSubstring(title1, title2);
		
		return (subStrLen*1.00/maxLen > 0.75) ? true : false;
	}
	
	/* 计算最长公共子序列长度 */
	private static int longestCommonSubstring(String title1, String title2) {
		char[] chars_title1 = title1.toCharArray();
		char[] chars_title2 = title2.toCharArray();
		int m = chars_title1.length;
		int n = chars_title2.length;
		int[][] matrix = new int[m + 1][n + 1];

		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				if (chars_title1[i - 1] == chars_title2[j - 1])
					matrix[i][j] = matrix[i - 1][j - 1] + 1;
				else
					matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
			}
		}
		return matrix[m][n];
	}
	
	/* 去除非文字符号 */
	private static String removeSign(String str) {
		StringBuffer strBuf = new StringBuffer();
		for (char item : str.toCharArray())
			if (isValidChar(item)){
				strBuf.append(item);
			}
		return strBuf.toString();
	}
	
	/* 是否汉字或字符 */
	private static boolean isValidChar(char charValue) {
		return (charValue >= 0x4E00 && charValue <= 0X9FA5)
				|| (charValue >= 'a' && charValue <= 'z')
				|| (charValue >= 'A' && charValue <= 'Z')
				|| (charValue >= '0' && charValue <= '9');
	}
	
	/* just for test */
	public static void main(String[] args) {
		String title1 = "亚冠月正初七打开 中超亚军夕除前夜";
		String title2 = "亚冠月正初七打开 中亚超军除夕前夜";
		PageInfo pg1 = new PageInfo(1);
		pg1.setTitle(title1);
		PageInfo pg2 = new PageInfo(2);
		pg2.setTitle(title2);
		System.out.println(isSimilarPage(pg1, pg2));
	}
	
} 
