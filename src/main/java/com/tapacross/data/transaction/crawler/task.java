package com.tapacross.data.transaction.crawler;

public class task {

	public static void main(String[] args) {
		
//        String input = "CA000001|CA000002";
//        String[] parts = input.split("\\|");
//        
//        for (String part : parts) {
//            System.out.println(part);
//        }
        
		String[] transferMultipleId = null;
		String categoryId ="CA000001|CA000002";
		categoryId = categoryId.replaceAll("\\|hide", "");
		if(categoryId.contains("|")) {
			System.out.println(categoryId.split("\\|")[0]);
		}else {
			System.out.println("difrrent Type, check categoryId: "+ categoryId);
		}
		System.out.println(transferMultipleId.length);
	}
}
