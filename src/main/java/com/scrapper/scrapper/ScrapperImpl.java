package com.scrapper.scrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.scrapper.scrapper.HTMLConst.Tag;

@Component
public class ScrapperImpl implements Scrapper {

	private static final Logger LOG = Logger.getLogger(ScrapperImpl.class);
	private static final String WEBSITE_ADDRESS = "http://bash.org.pl/latest/?page=";

	private static final String POST_CONTENT = "post-content";
	private static final String POST = "post";
	private static final String POINTS = "points";
	private static final String ID = "id";
	private static final String QID = "qid";
	private static final String CONTENT = "content";
	private static final String SLASH = "/";
	private static final String ACQUIRE_WEBSITE = "ACQUIRE_WEBSITE";
	private static final String EXTRACT_POST = "EXTRACT_POST";

	@Value("${file.localization}")
	private String fileLocalization;

	@Value("${file.name}")
	private String fileName;

	@Override
	public void retrieveLatest(int n) {
		Assert.isTrue(n > 0, "input parameter should be positive integer");
		try {
			int pageNumber = 1;
			List<JSONObject> toSave = new ArrayList<>();
			Elements elements = new Elements();
			while (elements.size() < n) {
				final String websiteAddress = new StringBuilder(WEBSITE_ADDRESS).append(pageNumber).toString();
				TimeMeasurement.measureTimeAndExecute(ACQUIRE_WEBSITE, () -> acquireWebsite(websiteAddress, elements));
				pageNumber++;
			}
			elements.stream().limit(n).forEach(e -> {
				Map<String, Object> values = TimeMeasurement.measureTimeAndExecute(EXTRACT_POST,
						() -> extractValues(e));
				JSONObject json = parseToJSON(values);
				toSave.add(json);
			});
			saveToFile(toSave);
			TimeMeasurement.printTimes();
		} catch (Exception e) {
			LOG.error("Error while retrieving data");
		}
	}

	private void acquireWebsite(final String websiteAddress, Elements elements) {
		try {
			Document document;
			document = Jsoup.connect(websiteAddress).get();
			elements.addAll(document.select(HTMLConst.combineByClass(Tag.DIV, POST)));
		} catch (IOException e) {
			LOG.error("Error connecting to website");
		}
	}

	private JSONObject parseToJSON(Map<String, Object> values) {
		JSONObject json = new JSONObject(values);
		return json;
	}

	private Map<String, Object> extractValues(Element element) {
		Map<String, Object> values = new HashMap<>();
		String idValue = element.select(HTMLConst.combineByClass(Tag.A, QID)).attr(HTMLConst.Attribute.HREF);
		idValue = StringUtils.delete(idValue, SLASH);
		Long id = Long.parseLong(idValue);
		Long points = Long.parseLong(element.select(HTMLConst.combineByClass(Tag.SPAN, POINTS)).html());
		String content = element.select(HTMLConst.combineByClass(Tag.DIV, POST_CONTENT)).html();
		values.put(ID, id);
		values.put(POINTS, points);
		values.put(CONTENT, content);
		return values;
	}

	private void saveToFile(List<JSONObject> toSave) throws IOException {
		String path = new StringBuilder(fileLocalization).append(fileName).toString();

		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
		toSave.forEach(t -> {
			try {
				fileWriter.append(t.toString());
			} catch (IOException e) {
				LOG.error("Error while writing to file");
			}
		});
		fileWriter.close();

	}
}
