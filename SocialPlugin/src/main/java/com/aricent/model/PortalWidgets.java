package com.aricent.model;

import java.util.List;

public class PortalWidgets {

	private String widgetId;
	private String widgetHeader;
	private List<String> pages;
	private Boolean addedToPage;
	private String previewDivContent;
	private Boolean addedToPortal;
	private String align;
	
	
	/**
	 * @return the widgetId
	 */
	public String getWidgetId() {
		return widgetId;
	}
	
	/**
	 * @param widgetId the widgetId to set
	 */
	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}
	/**
	 * @return the widgetHeader
	 */
	public String getWidgetHeader() {
		return widgetHeader;
	}
	/**
	 * @param widgetHeader the widgetHeader to set
	 */
	public void setWidgetHeader(String widgetHeader) {
		this.widgetHeader = widgetHeader;
	}
	/**
	 * @return the pagesList
	 */
	public List<String> getPagesList() {
		return pages;
	}
	/**
	 * @param pagesList the pagesList to set
	 */
	public void setPagesList(List<String> pages) {
		this.pages = pages;
	}
	/**
	 * @return the addedToPage
	 */
	public Boolean getAddedToPage() {
		return addedToPage;
	}
	/**
	 * @param addedToPage the addedToPage to set
	 */
	public void setAddedToPage(Boolean addedToPage) {
		this.addedToPage = addedToPage;
	}
	/**
	 * @return the previewDivContent
	 */
	public String getPreviewDivContent() {
		return previewDivContent;
	}
	/**
	 * @param previewDivContent the previewDivContent to set
	 */
	public void setPreviewDivContent(String previewDivContent) {
		this.previewDivContent = previewDivContent;
	}
	/**
	 * @return the addedToPortal
	 */
	public Boolean getAddedToPortal() {
		return addedToPortal;
	}
	/**
	 * @param addedToPortal the addedToPortal to set
	 */
	public void setAddedToPortal(Boolean addedToPortal) {
		this.addedToPortal = addedToPortal;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PortalWidgets [widgetId=" + widgetId + ", widgetHeader="
				+ widgetHeader + ", pages=" + pages + ", addedToPage="
				+ addedToPage + ", previewDivContent=" + previewDivContent
				+ ", addedToPortal=" + addedToPortal + "]";
	}

	/**
	 * @return the align
	 */
	public String getAlign() {
		return align;
	}

	/**
	 * @param align the align to set
	 */
	public void setAlign(String align) {
		this.align = align;
	}
	
}
