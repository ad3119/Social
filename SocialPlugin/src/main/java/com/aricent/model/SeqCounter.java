package com.aricent.model;

import org.springframework.data.annotation.Id;

public class SeqCounter {
	@Id
	private String _id;
	private int seq;
	
	public String get_id()
	{
		return _id;
	}
	public void set_id(String _id)
	{
		this._id = _id;
	}
	
	public int get_seq()
	{
		return seq;
	}
	public void setSeq(int seq)
	{
		this.seq=seq;
	}
}