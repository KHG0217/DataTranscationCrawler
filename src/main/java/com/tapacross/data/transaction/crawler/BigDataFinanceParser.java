package com.tapacross.data.transaction.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tapacross.data.transaction.crawler.VO.dataTranscationVO;
import com.tapacross.data.transcation.util.JsoupUtil;

public class BigDataFinanceParser {
	private XSSFWorkbook workbook;
	private int PARSE_SLEEP = 3000;
	private String EXCEL_CREATE_PATH ="C:\\home\\금융빅데이터플랫폼\\";

	public Response parseData(String url, String requestBody) {
		Response res = null;
		try {
				res = JsoupUtil.getJsoupSecureConnection(url)
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
					.header("Accept-Encoding", "gzip, deflate, br")
					.header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
					.header("Host", "www.bigdata-finance.kr")
					.header("Referer", "https://www.bigdata-finance.kr/dataset/datasetList.do")
					.header("Origin", "https://www.bigdata-finance.kr")
					.maxBodySize(0)
					.method(Method.POST)
					.requestBody(requestBody)
//					.execute();
					.ignoreContentType(true).execute();
			Thread.sleep(PARSE_SLEEP);
					
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * 
	 * @param url
	 * @param requestBodyForSuperCategory
	 * @return bdfCategoryList
	 * @see bdf대분류를 추출하여 Map에 담는다.
	 * Map key = 카테고리명
	 * Map value = 카테고리ID(해당 id로 카테고리별 데이터를 수집한다.)
	 */
	public List<Map<String,String>> extractKdxSuperCategory(String url, String requestBodyForSuperCategory){
		List<Map<String,String>> bdfCategoryList = new ArrayList<>();
		
		Response res = parseData(url, requestBodyForSuperCategory);
		Document doc = Jsoup.parse(res.body());
		
		Elements els = doc.select("div.category > div > ul > li");
		for(Element el : els) {
			Map<String,String> superCategoryMap = new HashMap<>();
			
			String superCategoryName = el.text();
			if(superCategoryName.equals("전체")) {
				continue;
			}
			
			String superCategoryId = el.select("a").attr("onclick")
					.replaceAll("searchType", "")
					.replaceAll("\\(", "")
					.replaceAll("\\)", "")
					.split(",")[2]
					.replaceAll("\\'", "")
					.replaceAll("\\;", "");
			
			superCategoryMap.put(superCategoryName, superCategoryId);
			bdfCategoryList.add(superCategoryMap);
		}
		
		return bdfCategoryList;
	}
	
	/**
	 * 
	 * @param url
	 * @param requestBodyForBaseCategory
	 * @param superCategory
	 * @return List<dataTranscationVO>
	 * @see bdf중분류 url, requestBody 를받아 
	 * 중분류명, 수집에 필요한 datastID값, 중분류 카테고리 ID값을 담은 VO를 List에 담아 반환한다.
	 * 
	 */
	public List<dataTranscationVO> extractBaseCategory(String url, String requestBodyForBaseCategory ,String superCategory) {
		List<dataTranscationVO> baseCategoryList = new ArrayList<>();
		
		Response res = parseData(url, requestBodyForBaseCategory);
		Document doc = Jsoup.parse(res.body());
		
		Elements els = doc.select("#contain > div.list.Type1 > ul > li > a");
		for(Element el : els) {
			dataTranscationVO bdfData = new dataTranscationVO();
			String baseCategoryName = el.select("img").attr("alt");
			String datastId = el.attr("onclick")
					.replaceAll("goView", "")
					.replaceAll("\\(", "")
					.replaceAll("\\)", "")
					.replaceAll("\\'", "")
					.trim();
			
			bdfData.setSuperCategory(superCategory);
			bdfData.setBaseCategory(baseCategoryName); // 중분류 제목
			bdfData.setEtc1(datastId); // 상세 데이터 수집을위한 id값
			baseCategoryList.add(bdfData);
			}
		System.out.println("baseCategoryList size: " + baseCategoryList.size());
		return baseCategoryList;
	}
	  
	/**
	 * 
	 * @param url
	 * @param requestBodyForData
	 * @param superCategory
	 * @param baseCategory
	 * @return List<dataTranscationVO>
	 * @see kdx data jsondata를받아 dataTranscationVO에 담고 List에 반환한다.
	 * 
	 * 
	 * 
	 */
	public List<dataTranscationVO> extractBdfData(String url, String requestBodyForData, String superCategory ,String baseCategory) {
		List<dataTranscationVO> dataList = new ArrayList<>();
		
		Response res = parseData(url, requestBodyForData);
		Document doc = Jsoup.parse(res.body());
		
		Elements els = doc.select("#contain > div.goods > div.goods-price > div.choice > ul > li ");
//		String updateDate = doc.select("#contain > div.goods > div.goods-detail > table > tbody > tr:nth-child(3) > td:nth-child(2)").text();
//		String createDate = doc.select("#contain > div.goods > div.goods-detail > table > tbody > tr:nth-child(3) > td:nth-child(4)").text();
		String provider = doc.select("#contain > div.goods > div.goods-detail > table > tbody > tr:nth-child(2) > td:nth-child(2) > button").text();
		for(Element el : els) {
			dataTranscationVO bdfData = new dataTranscationVO();
			
			if(el.select("p > strong").text().equals("데이터 전체")) {
				continue;
			}
			
			bdfData.setEtc2(provider); // 제공기관
			
			String title = el.select("span.dataNm").text();
			bdfData.setTitle(title);
			
			String price = el.select("span.dataAmt").text();
			bdfData.setPrice(price);
			
			bdfData.setSuperCategory(superCategory);
			bdfData.setBaseCategory(baseCategory);
			
			dataList.add(bdfData);
			
		}
		System.out.println("dataList size: " + dataList.size());
		return dataList;
	}
	
	/**
	 * 
	 * @param excelName
	 * @param category
	 * @param dataList
	 * @see 엑셀이름과 bdf카테고리, 카테고리별 데이터가 들어있는 bdf 데이터 List를 인자로 받아, 엑셀로 도식화한다.
	 * 엑셀이름은 해당 메소드를 호출하는 쪽에서 만들어준다.
	 * 같은 엑셀에 카테고리별 시트만 추가하여 하나의 엑셀로 만들기 위하여 XSSFWorkbook 객체는 전역변수에 선언하고,
	 * 파일이 존재하지 않는다면 생성한다.
	 * 
	 */
	private void createExcelFile(String excelName, String baseCategory, List<dataTranscationVO> dataList) {
		try {
	
			File file = new File(EXCEL_CREATE_PATH + excelName);
			if (!file.exists()) {
				this.workbook = new XSSFWorkbook();
				System.out.println("create excel file, path: " + EXCEL_CREATE_PATH + excelName);
			}
			
			int sheetCount = 2;
			while(this.workbook.getSheet(baseCategory) != null) {
				baseCategory = baseCategory + "-" + String.valueOf(sheetCount);
				sheetCount ++;
			}
			
			XSSFSheet sheet = this.workbook.createSheet(baseCategory);
			String[] columnName = {"제목","가격","제공기관", "카테고리"};
	
			int rowNum = 0;
			int columnNamelineNum = 0;
			XSSFRow columRow = sheet.createRow(rowNum);
			for (String rowData : columnName) {
				XSSFCell cell = columRow.createCell(columnNamelineNum);
				cell.setCellValue(rowData);
				sheet.autoSizeColumn(columnNamelineNum);
				sheet.setColumnWidth(columnNamelineNum, (sheet.getColumnWidth(columnNamelineNum)) + 6000);
				columnNamelineNum ++;
			}
			rowNum ++;
			
			for(dataTranscationVO data : dataList) {
				int lineNum = 0;
				
				XSSFRow dataRow = sheet.createRow(rowNum);
				XSSFCell titleCell = dataRow.createCell(lineNum);
				titleCell.setCellValue(data.getTitle());
				lineNum ++;
							
				XSSFCell priceCell = dataRow.createCell(lineNum);
				priceCell.setCellValue(data.getPrice());
				lineNum ++;
				
				XSSFCell providerCell = dataRow.createCell(lineNum);
				providerCell.setCellValue(data.getEtc2());
				lineNum ++;
								
				XSSFCell categoryCell = dataRow.createCell(lineNum);
				categoryCell.setCellValue(data.getSuperCategory());
				lineNum ++;
				
				rowNum ++;
			}
			FileOutputStream outputStream = new FileOutputStream(EXCEL_CREATE_PATH + excelName);
			this.workbook.write(outputStream);
			outputStream.close();
			System.out.println("Add Excel Sheets, Sheets: " + baseCategory);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
		
	void main() {
		
		System.out.println("BDF data Transcation Cralwer start");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddkkmm");
		String currentDate = timeFormat.format(cal.getTime());
		
		String superCategoryUrl = "https://www.bigdata-finance.kr/dataset/datasetList.do";
		String superCategoryRequestBody = "pageNum=1&classNew=&classList=&classDown=&orderSort=&orderSortScroll=0&recommendData=&dataParameter=&datastId=&ctlgId=&searchTxt=&pageSize=10&searchKeywordTxt=&typeFilterTxt=&providerFilterTxt=&dateFilterTxt=&priceFilterTxt=";
//		String superCategoryData = parseData(KDX_SUPER_CATEGORY_DATA_API_URL,superCategoryRequestBody).body();
		List<Map<String,String>> categoryList = extractKdxSuperCategory(superCategoryUrl,superCategoryRequestBody);
		
		List<dataTranscationVO> baseCategoryList = new ArrayList<dataTranscationVO>();
		
		for(Map<String,String> category: categoryList) {
			String baseCategoryId = category.values().toString()
					.replaceAll("\\[", "")
					.replaceAll("\\]", "")
					.trim();
			
			String baseCategoryUrl = "https://www.bigdata-finance.kr/dataset/datasetList.do";
			String requestBodyForBaseCategory = "pageNum=1&classNew=&classList=&classDown=&orderSort=&orderSortScroll=0&recommendData=&dataParameter=&datastId=&ctlgId="
					+ baseCategoryId
					+ "&searchTxt=&pageSize=100&searchKeywordTxt=&typeFilterTxt=&providerFilterTxt=&dateFilterTxt=&priceFilterTxt=";
			String superCategory = category.keySet().toString()
					.replaceAll("\\[", "")
					.replaceAll("\\]", "")
					.trim();
			baseCategoryList = extractBaseCategory(baseCategoryUrl, requestBodyForBaseCategory, superCategory);	
			
			for(dataTranscationVO baseCategoryData : baseCategoryList) {
				System.out.println(baseCategoryData.getBaseCategory());

				String baseCategotryExcelName = baseCategoryData.getSuperCategory()
						.replaceAll("\\/", ",")
						.replaceAll("\\[", "")
						.replaceAll("\\]", "")
						.replaceAll("\\*", "")
						.replaceAll("\\\\", "")
						.replaceAll("\\:", "")
						.replaceAll("\\?", "");
				String excelName = "금융빅데이터플랫폼" + "_" + baseCategotryExcelName + "_" + currentDate +".xlsx";
				String datastId = baseCategoryData.getEtc1();
				
				String bdfDataUrl = "https://www.bigdata-finance.kr/dataset/datasetView.do";
				String requestBodyForSmallCategoryData ="pageNum=1&classNew=&classList=&classDown=&orderSort=&orderSortScroll=0&recommendData=&dataParameter="
						+ "&datastId="
						+ datastId
						+ "&ctlgId="
						+ baseCategoryId
						+ "&searchTxt=&pageSize=500&searchKeywordTxt=&typeFilterTxt=&providerFilterTxt=&dateFilterTxt=&priceFilterTxt=";

				List<dataTranscationVO> dataList = extractBdfData(bdfDataUrl, requestBodyForSmallCategoryData, baseCategoryData.getSuperCategory() , baseCategoryData.getBaseCategory());	
				String baseCategorySheetName = baseCategoryData.getBaseCategory()
						.replaceAll("\\/", ",")
						.replaceAll("\\[", "")
						.replaceAll("\\]", "")
						.replaceAll("\\*", "")
						.replaceAll("\\\\", "")
						.replaceAll("\\:", "")
						.replaceAll("\\?", "");
				createExcelFile(excelName, baseCategorySheetName, dataList);				
			}			
		}
	}
		
	public static void main(String[] args) {
		BigDataFinanceParser BDFParser = new BigDataFinanceParser();
		BDFParser.main();
	}
}
