/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.dashboard.services;

public class DataItem {
	private Long datetime;
	private String id;
	private String content;

	public DataItem(String id, String content, Long datetime) {
		this.id = id;
		this.datetime = datetime;
		this.content = content;
	}
	
	/**@Return data time in milliseconds **/
	public Long getDatetime() {
		return datetime;
	}
	
	public void setDatetime(Long datetime) {
		this.datetime = datetime;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getId() {
		return id;
	}
	

}
