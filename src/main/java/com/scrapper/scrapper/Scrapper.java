package com.scrapper.scrapper;

public interface Scrapper {
	/**
	 * downloads n sites, parsing them to JSON and saves to file
	 *
	 * @param n
	 *            - number of sites to retrieve
	 *
	 * @return
	 */
	void retrieveLatest(int n);
}
