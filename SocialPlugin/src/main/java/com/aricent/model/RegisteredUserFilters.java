package com.aricent.model;

import java.util.Date;

public class RegisteredUserFilters {

	private Date startDate;
    private Date endDate;
    private String contextId;
    
    private int registeredUserCount;
    private long totalUserCount;

    public Date getStartDate() {
           return startDate;
    }
    public void setStartDate(Date startDate) {
           this.startDate = startDate;
    }
    public Date getEndDate() {
           return endDate;
    }
    public void setEndDate(Date endDate) {
           this.endDate = endDate;
    }
    public String getContextId() {
           return contextId;
    }
    public void setContextId(String contextId) {
           this.contextId = contextId;
    }      
    public int getRegisteredUserCount() {
           return registeredUserCount;
    }
    public void setRegisteredUserCount(int registeredUserCount) {
           this.registeredUserCount = registeredUserCount;
    }
    public long getTotalUserCount() {
           return totalUserCount;
    }
    public void setTotalUserCount(long totalUserCount) {
           this.totalUserCount = totalUserCount;
    }

}
