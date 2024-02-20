package com.tapacross.transcation.cralwer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.tapacross.data.transaction.crawler.KdxParser;
import com.tapacross.data.transaction.crawler.VO.dataTranscationVO;

import junit.framework.Assert;

public class KdxParserTest {

	@Test
	public void testSuperCategoryParse() {
		KdxParser pr = new KdxParser();
		String url = "https://kdx.kr/category/getCategoryList";
		String requestBody = "status=active";
		
		int statusCode = pr.parseData(url, requestBody).statusCode();
		Assert.assertEquals(200, statusCode);
	}
	
	@Test
	public void testBaseCategoryParse(){
		KdxParser pr = new KdxParser();
		String url = "https://kdx.kr/product/getSpecsList";
		String catecoryId = "CA000001";
		String requestBody = "page_num=0&page_size=100&category_arr=" + catecoryId + "&sort_type=desc&price=&price_end=";
		
		int statusCode = pr.parseData(url, requestBody).statusCode();
		Assert.assertEquals(200, statusCode);
	}
	
	@Test
	public void testDataParse(){
		KdxParser pr = new KdxParser();
		String url = "https://kdx.kr/product/getProList";
		String dataRequestCategoryIdtext = "category_arr=CA000001";
		String specsId = "MA20240006";
		String requestBody = "page_num=0&page_size=300&" + dataRequestCategoryIdtext + 
		"category_arr=hide&specs_id=" + specsId +
		"&sort_type=desc&search_keyword=&search_type=multiple";
		
		int statusCode = pr.parseData(url, requestBody).statusCode();
		Assert.assertEquals(200, statusCode);
	}
	
	@Test
	public void extractKdxSuperCategoryTest() {
		KdxParser pr = new KdxParser();
		String jsonData = null;
		String filePath = "../data-transaction-crawler/src/main/java/com/tapacross/data/transaction/data/kdxSuperCatrgory.json";
		
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				jsonData = line;
			}
			
			br.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
		
		String url = "https://kdx.kr/category/getCategoryList";
		String requestBody = "status=active";
		jsonData = pr.parseData(url, requestBody).body();
		
		List<Map<String,String>> superCategoryList = pr.extractKdxSuperCategory(jsonData);
		System.out.println(superCategoryList);
		
	}
	
	@Test
	public void extractBaseCategoryTest() {
		
		// test - json파일
		String filePath = "../data-transaction-crawler/src/main/java/com/tapacross/data/transaction/data/kdxBaseCategory.json";
		KdxParser pr = new KdxParser();
		String jsonData = null;
		
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				jsonData = line;
			}
			
			br.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		

//		String url = "https://kdx.kr/product/getSpecsList";
//		String catecoryId = "CA000001";
//		String requestBody = "page_num=0&page_size=100&category_arr=" + catecoryId + "&sort_type=desc&price=&price_end=";	
//		jsonData = pr.parseData(url, requestBody).body();

		String superCategory = "금융,증권";
		List<dataTranscationVO> baseCategoryList = pr.extractBaseCategory(jsonData, superCategory);
		System.out.println(baseCategoryList);
	}
	
	@Test
	public void extractKdxDataTest() {
		// test - json파일
		String filePath = "../data-transaction-crawler/src/main/java/com/tapacross/data/transaction/data/kdxData.json";
		KdxParser pr = new KdxParser();
		String jsonData = null;
		
		try {
			FileReader fr = new FileReader(filePath);
			BufferedReader br = new BufferedReader(fr);
			
			String line;
			
			while ((line = br.readLine()) != null) {
				jsonData = line;
			}
			
			br.close();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
//		String url = "https://kdx.kr/product/getProList";
//		String dataRequestCategoryIdtext = "category_arr=CA000001";
//		String specsId = "MA20240006";
//		String requestBody = "page_num=0&page_size=300&" + dataRequestCategoryIdtext + 
//		"category_arr=hide&specs_id=" + specsId +
//		"&sort_type=desc&search_keyword=&search_type=multiple";
//		jsonData = pr.parseData(url, requestBody).body();
		
		String superCategory = null;
		String baseCategory = null;
		
		List<dataTranscationVO> baseCategoryList = pr.extractKdxData(jsonData,superCategory,baseCategory);
		System.out.println(baseCategoryList);
	}
	
}
