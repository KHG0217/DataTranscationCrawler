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
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tapacross.data.transaction.crawler.VO.dataTranscationVO;
import com.tapacross.data.transcation.util.JsoupUtil;

public class KdxParser {
		
	private XSSFWorkbook workbook;
	private int PARSE_SLEEP = 3000;
	private String KDX_SUPER_CATEGORY_DATA_API_URL = "https://kdx.kr/category/getCategoryList";
	private String KDX_BASE_CATEGORY_API_URL = "https://kdx.kr/product/getSpecsList";
	private String KDX_DATA_API_URL = "https://kdx.kr/product/getProList";

	public Response parseData(String url, String requestBody) {
		Response res = null;
		try {
				res = JsoupUtil.getJsoupSecureConnection(url)
					.header("Accept", "application/json, text/javascript, */*; q=0.01")
					.header("Accept-Encoding", "gzip, deflate, br")
					.header("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.5,en;q=0.3")
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
					.header("Host", "kdx.kr")
					.header("Referer", "https://kdx.kr/data/data-all")
					.header("X-Requested-With", " XMLHttpRequest")
					.header("Authorization", "kdx2023checker")
					.maxBodySize(0)
					.method(Method.POST)
					.requestBody(requestBody)
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
	 * @param jsonData
	 * @return kdxCategoryList
	 * @see kdx대분류 jsondata를 받아 Map에 담는다.
	 * Map key = 카테고리명
	 * Map value = 카테고리ID(해당 id로 카테고리별 데이터를 수집한다.)
	 */
	public List<Map<String,String>> extractKdxSuperCategory(String jsonData){
		List<Map<String,String>> kdxCategoryList = new ArrayList<>();
		
		JsonObject obj = JsonParser.parseString(jsonData).getAsJsonObject();
		JsonArray ary = obj.getAsJsonObject("result").getAsJsonArray("data");
		
		for(JsonElement jel : ary) {
			Map<String,String> kdxCategory = new HashMap<>();
			String categroy = jel.getAsJsonObject().get("category_name").getAsString();
			String categeyId = jel.getAsJsonObject().get("category_id").getAsString();
			
			kdxCategory.put(categroy, categeyId);
			kdxCategoryList.add(kdxCategory);
		}
		System.out.println("kdx category size: " + kdxCategoryList.size());
		System.out.println("category List: " + kdxCategoryList);
		return kdxCategoryList;
	}
	
	/**
	 * 
	 * @param jsonData
	 * @return List<dataTranscationVO>
	 * @see kdx중분류 jsondata를받아 
	 * 중분류명, 수집에 필요한 corp_id값, specs_id값을 담은 VO를 List에 담아 반환한다.
	 * 
	 */
	public List<dataTranscationVO> extractBaseCategory(String jsonData, String superCategory) {
		List<dataTranscationVO> baseCategoryList = new ArrayList<>();
		
		JsonObject obj = JsonParser.parseString(jsonData).getAsJsonObject();
		JsonArray ary = obj.getAsJsonObject("result").getAsJsonArray("data");
		for(JsonElement jel : ary) {
			dataTranscationVO kdxData = new dataTranscationVO();
			
			String title = jel.getAsJsonObject().get("specs_nm").getAsString();
//			String createDate = jel.getAsJsonObject().get("created_date").getAsString();
//			String modifiedDate = jel.getAsJsonObject().get("modified_date").getAsString();
//			String categeyName = jel.getAsJsonObject().get("category_name").getAsString();
//			String price = obj2.getAsJsonObject().get("price").getAsString(); //파싱은 가능하나, 객체에 담지않는다.
//			String hits = jel.getAsJsonObject().get("hits").getAsString();
//			String productCnt= obj2.getAsJsonObject().get("product_cnt").getAsString(); // 상품 상세 갯수는 파싱은 가능하나, 객체에 담지않는다.
			String categoryId = jel.getAsJsonObject().get("category_id").getAsString();
			String specsId = jel.getAsJsonObject().get("specs_id").getAsString();
			
			kdxData.setSuperCategory(superCategory);
			kdxData.setBaseCategory(title); // 중분류 제목
			kdxData.setEtc1(categoryId); // 상세 데이터 수집을위한 id값
			kdxData.setEtc2(specsId); // 상세 데이터 수집을위한 id값 
			
			baseCategoryList.add(kdxData);
		}
		System.out.println("baseCategoryList size: " + baseCategoryList.size());
//		System.out.println("dataList List: " + dataList);
		return baseCategoryList;
	}
	  
	/**
	 * 
	 * @param jsonData
	 * @return List<dataTranscationVO>
	 * @see kdx data jsondata를받아 dataTranscationVO에 담고 List에 반환한다.
	 * 
	 * 
	 * 
	 */
	public List<dataTranscationVO> extractKdxData(String jsonData, String superCategory ,String baseCategory) {
		List<dataTranscationVO> dataList = new ArrayList<>();
		
		JsonObject obj = JsonParser.parseString(jsonData).getAsJsonObject();
		JsonArray ary = obj.getAsJsonObject("result").getAsJsonArray("data");
		System.out.println(ary.size());
		for(JsonElement jel : ary) {
			dataTranscationVO kdxData = new dataTranscationVO();
			
			try {
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			String title = jel.getAsJsonObject().get("title").getAsString()
					.replaceAll("&#39;", "'"); // &#39; -> ' 변환
			String createDate = jel.getAsJsonObject().get("created_date").getAsString();
			String modifiedDate = jel.getAsJsonObject().get("modified_date").getAsString();
			
			String categeyName = null;
			if(jel.getAsJsonObject().get("category").isJsonNull()) {
				categeyName = null;
			}else {
				categeyName = jel.getAsJsonObject().get("category").getAsString();
			}
			String hits = jel.getAsJsonObject().get("hits").getAsString();
			
			kdxData.setTitle(title);
			kdxData.setCreateDate(createDate);
			kdxData.setUpdateDate(modifiedDate);
			kdxData.setHitCount(hits);	
			kdxData.setSuperCategory(categeyName);
			kdxData.setSuperCategory(superCategory);
			kdxData.setBaseCategory(baseCategory);
			
			dataList.add(kdxData);
		}
		System.out.println("dataList size: " + dataList.size());
//		System.out.println("dataList List: " + dataList);
		return dataList;
	}
	
	/**
	 * 
	 * @param excelName
	 * @param category
	 * @param dataList
	 * @see 엑셀이름과 kdx카테고리, 카테고리별 데이터가 들어있는 kdx 데이터 List를 인자로 받아, 엑셀로 도식화한다.
	 * 엑셀이름은 해당 메소드를 호출하는 쪽에서 만들어준다.
	 * 같은 엑셀에 카테고리별 시트만 추가하여 하나의 엑셀로 만들기 위하여 XSSFWorkbook 객체는 전역변수에 선언하고,
	 * 파일이 존재하지 않는다면 생성한다.
	 * 
	 */
	private void createExcelFile(String excelName, String baseCategory, List<dataTranscationVO> dataList) {
		try {
			String rootPath = "C:\\home\\kdx데이터거래소\\";
					
			File file = new File(rootPath + excelName);
			if (!file.exists()) {
				this.workbook = new XSSFWorkbook();
				System.out.println("create excel file, path: " + rootPath + excelName);
			}
			
			int sheetCount = 2;
			while(this.workbook.getSheet(baseCategory) != null) {
				baseCategory = baseCategory + "-" + String.valueOf(sheetCount);
				sheetCount ++;
			}
			
			XSSFSheet sheet = this.workbook.createSheet(baseCategory);
			String[] columnName = {"제목","최초 등록날짜","최근 수정날짜", "게시물 방문횟수", "카테고리"};
	
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
							
				XSSFCell createDateCell = dataRow.createCell(lineNum);
				createDateCell.setCellValue(data.getCreateDate());
				lineNum ++;
				
				XSSFCell updateDateCell = dataRow.createCell(lineNum);
				updateDateCell.setCellValue(data.getUpdateDate());
				lineNum ++;
				
				XSSFCell hitCountCell = dataRow.createCell(lineNum);
				hitCountCell.setCellValue(data.getHitCount());
				lineNum ++;
				
				XSSFCell categoryCell = dataRow.createCell(lineNum);
				categoryCell.setCellValue(data.getSuperCategory());
				lineNum ++;
				
				rowNum ++;
			}
			FileOutputStream outputStream = new FileOutputStream(rootPath + excelName);
			this.workbook.write(outputStream);
			outputStream.close();
			System.out.println("Add Excel Sheets, Sheets: " + baseCategory);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private String[] transferMultipleCatecoryId(String categoryId) {
		String[] transferMultipleId = null;
		if(categoryId.contains("|")) {
			transferMultipleId = categoryId.split("\\|");
		}else {
			System.out.println("difrrent Type, check categoryId: "+ categoryId);
		}
				
		return transferMultipleId;
	}
	
	void main() {
		
		System.out.println("KDX data Transcation Cralwer start");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddkkmm");
		String currentDate = timeFormat.format(cal.getTime());
		
		String superCategoryRequestBody = "status=active";
		String superCategoryData = parseData(KDX_SUPER_CATEGORY_DATA_API_URL,superCategoryRequestBody).body();
		List<Map<String,String>> categoryList = extractKdxSuperCategory(superCategoryData);
		
		List<dataTranscationVO> baseCategoryList = new ArrayList<dataTranscationVO>();
		
		for(Map<String,String> category: categoryList) {
			String catecoryIdTransfer = category.values().toString()
					.replaceAll("\\[", "")
					.replaceAll("\\]", "");
			String baseCategoryRequestBody = "page_num=0&page_size=100&category_arr=" + catecoryIdTransfer + "&sort_type=desc&price=&price_end=";
			String baseCategoryJsonData = parseData(KDX_BASE_CATEGORY_API_URL, baseCategoryRequestBody).body();
	
			String categoryTransfer = category.keySet().toString()
					.replaceAll("\\/", ",")
					.replaceAll("\\[", "")
					.replaceAll("\\]", "");
			baseCategoryList = extractBaseCategory(baseCategoryJsonData, categoryTransfer);	
			
			for(dataTranscationVO baseCategoryData : baseCategoryList) {
				System.out.println(baseCategoryData.getBaseCategory());

				String excelName = "kdx데이터거래소" + "_" + baseCategoryData.getSuperCategory() + "_" + currentDate +".xlsx";
				
				String categoryId = baseCategoryData.getEtc1()
						.replaceAll("\\|hide", ""); // 불필요한 값 제거 ex) CA000001|hide
				String specsId = baseCategoryData.getEtc2();
				String dataRequestCategoryIdtext = null;
				
				if(categoryId.contains("|")) { // 중분류 카테고리 id값이 중복으로 들어오는 경우 ex) CA000001|CA000002
					String[] catecoryIdArry = transferMultipleCatecoryId(categoryId);
					
					for(String catecoryIdArryValue: catecoryIdArry) {
						dataRequestCategoryIdtext = dataRequestCategoryIdtext + "category_arr=" + catecoryIdArryValue +"&";
					}
				}else {
					dataRequestCategoryIdtext = "category_arr=" + categoryId +"&";
				}
				
				String dataRequestBody = "page_num=0&page_size=300&" + dataRequestCategoryIdtext + 
				"category_arr=hide&specs_id=" + specsId +
				"&sort_type=desc&search_keyword=&search_type=multiple";
				
				String kdxContentJsonData = parseData(KDX_DATA_API_URL, dataRequestBody).body();
				List<dataTranscationVO> dataList = extractKdxData(kdxContentJsonData, baseCategoryData.getSuperCategory() , baseCategoryData.getBaseCategory());	
				String baseCategorySheetName = baseCategoryData.getBaseCategory()
						.replaceAll("\\/", ",")
						.replaceAll("\\[", "")
						.replaceAll("\\]", "")
						.replaceAll("\\*", "");
						
				createExcelFile(excelName, baseCategorySheetName, dataList);				
			}			
		}
	}
		
	public static void main(String[] args) {
		KdxParser kdxParser = new KdxParser();
		kdxParser.main();
	}
}
