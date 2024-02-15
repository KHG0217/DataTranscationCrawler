package com.tapacross.data.transaction.crawler.VO;

public class dataTranscationVO {

	private String title;
	private String categoey;
	private String createDate;
	private String updateDate;
	private String hitCount;
	
	public String getTitle() {
		return title;
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
	public String getCategoey() {
		return categoey;
	}
	public void setCategoey(String categoey) {
		this.categoey = categoey;
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
	@Override
	public String toString() {
		return "dataTranscationVO [title=" + title + ", categoey=" + categoey + ", createDate=" + createDate
				+ ", updateDate=" + updateDate + ", hitCount=" + hitCount + "]";
	}

}
