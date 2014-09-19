package com.gso.hogoapi.model;

public class SignUpData {
	private boolean status;
	private String desc;

	/**
	 * @return the statues
	 */
	public boolean isSuccess() {
		return status;
	}

	/**
	 * @param statues
	 *            the statues to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the token
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param Descsription
	 *            the Descsription to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
}
