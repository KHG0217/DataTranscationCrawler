package com.tapacross.data.transaction.crawler.VO;

public class dataTranscationVO {

	private String title;
	private String createDate;
	private String updateDate;
	private String hitCount;
	private String superCategory;
	private String baseCategory;
	private String smallCategory;
	private String etc1;
	private String etc2;
	private String price;
	
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getTitle() {
		return title;
	}
	
	public String getSuperCategory() {
		return superCategory;
	}

	public void setSuperCategory(String superCategory) {
		this.superCategory = superCategory;
	}

	public String getBaseCategory() {
		return baseCategory;
	}
	public void setBaseCategory(String baseCategory) {
		this.baseCategory = baseCategory;
	}
	public String getSmallCategory() {
		return smallCategory;
	}
	public void setSmallCategory(String smallCategory) {
		this.smallCategory = smallCategory;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHitCount() {
		return hitCount;
	}
	public void setHitCount(String hitCount) {
		this.hitCount = hitCount;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
		
	public String getEtc1() {
		return etc1;
	}
	public void setEtc1(String etc1) {
		this.etc1 = etc1;
	}
	public String getEtc2() {
		return etc2;
	}
	public void setEtc2(String etc2) {
		this.etc2 = etc2;
	}

	@Override
	public String toString() {
		return "dataTranscationVO [title=" + title + ", createDate=" + createDate + ", updateDate=" + updateDate
				+ ", hitCount=" + hitCount + ", superCategory=" + superCategory + ", baseCategory=" + baseCategory
				+ ", smallCategory=" + smallCategory + ", etc1=" + etc1 + ", etc2=" + etc2 + ", price=" + price + "]";
	}


}
