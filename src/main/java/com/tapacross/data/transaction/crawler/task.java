package com.tapacross.data.transaction.crawler;

import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class task {

	public static void main(String[] args) {
		
		task pr = new task();
		pr.bdfSuperCategoryParseTest();
	}
	
	private void bdfSuperCategoryParseTest(){
		BigDataFinanceParser pr = new BigDataFinanceParser();
		String url = "https://www.bigdata-finance.kr/dataset/datasetList.do";
		String requestBody = "pageNum=1&classNew=&classList=&classDown=&orderSort=&orderSortScroll=0&recommendData=&dataParameter=&datastId=&ctlgId=&searchTxt=&pageSize=10&searchKeywordTxt=&typeFilterTxt=&providerFilterTxt=&dateFilterTxt=&priceFilterTxt=";
		
		List<Map<String, String>> map = pr.extractKdxSuperCategory(url,requestBody);
//		System.out.println(map);
		
		for(Map<String,String> superCategoryMap : map ) {
			String requestBodySuperCategory = superCategoryMap.values().toString()
					.replaceAll("\\[", "")
					.replaceAll("\\]", "")
					.trim();
			String requestBodyForBaseCategory = "pageNum=1&classNew=&classList=&classDown=&orderSort=&orderSortScroll=0&recommendData=&dataParameter=&datastId=&ctlgId="
					+ requestBodySuperCategory
					+ "&searchTxt=&pageSize=100&searchKeywordTxt=&typeFilterTxt=&providerFilterTxt=&dateFilterTxt=&priceFilterTxt=";
			
			System.out.println("현재 parser중인 카테고리 : " + superCategoryMap.keySet().toString());
			Response res2 = pr.parseData(url, requestBodyForBaseCategory);
			Document doc2 = Jsoup.parse(res2.body());
			
			Elements els2 = doc2.select("#contain > div.list.Type1 > ul > li > a");
			System.out.println("사이즈: " + els2.size());
			for(Element el2 : els2) {
				String baseCategoryName = el2.select("img").attr("alt");
				String datastId = el2.attr("onclick")
						.replaceAll("goView", "")
						.replaceAll("\\(", "")
						.replaceAll("\\)", "")
						.replaceAll("\\'", "")
						.trim();
				System.out.println("중카테고리이름: " + baseCategoryName);
				System.out.println("중카테고리ID: " + datastId);
				
				String url2 = "https://www.bigdata-finance.kr/dataset/datasetView.do";
				String requestBodyForSmallCategoryData ="pageNum=1&classNew=&classList=&classDown=&orderSort=&orderSortScroll=0&recommendData=&dataParameter="
						+ "&datastId="
						+ datastId
						+ "&ctlgId="
						+ requestBodySuperCategory
						+ "&searchTxt=&pageSize=500&searchKeywordTxt=&typeFilterTxt=&providerFilterTxt=&dateFilterTxt=&priceFilterTxt=";
				Response res3 = pr.parseData(url2, requestBodyForSmallCategoryData);
				Document doc3 = Jsoup.parse(res3.body());
				
				Elements els3 = doc3.select("#contain > div.goods > div.goods-price > div.choice > ul > li ");
				System.out.println("상품사이즈: " + els3.size());
				String provider = doc3.select("#contain > div.goods > div.goods-detail > table > tbody > tr:nth-child(2) > td:nth-child(2) > button").text();
				for(Element el3 : els3) {
					if(el3.select("p > strong").text().equals("데이터 전체")) {
						System.out.println("pass");
						continue;
					}
					System.out.println("상품명: " + el3.select("span.dataNm").text());
					System.out.println("상품가격: " + el3.select("span.dataAmt").text());
					System.out.println("제공기관: " + provider);

				}
			}
			
//			try {
//				Thread.sleep(3000);
//			}catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
		}
	}
}
