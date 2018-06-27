package com.scrapper.scrapper;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.assertj.core.util.Files;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScrapperImplTest {

	@Value("${file.localization}")
	private String fileLocalization;

	@Value("${file.name}")
	private String fileName;

	private File file;

	@Autowired
	Scrapper underTest;

	@Before
	public void beforeTest() {
		file = new File(fileLocalization + fileName);
		file.delete();
	}

	@Test
	public void shouldCreateValidFile() {
		underTest.retrieveLatest(10);
		assertThat(file.exists(), is(true));
		String content = Files.contentOf(file, "utf-8");

		assertThat(content, CoreMatchers.containsString("\"id\":"));
		assertThat(StringUtils.countOccurrencesOf(content, "\"id\":"), is(10));
		assertThat(content, CoreMatchers.containsString("points"));
		assertThat(content, CoreMatchers.containsString("content"));
	}

	@Test
	public void shouldRetrieveForMoreSites() {
		underTest.retrieveLatest(1000);
		assertThat(file.exists(), is(true));
		String content = Files.contentOf(file, "utf-8");

		assertThat(content, CoreMatchers.containsString("\"id\":"));
		assertThat(StringUtils.countOccurrencesOf(content, "\"id\":"), is(1000));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldAssertInput() {
		underTest.retrieveLatest(0);
	}

	@Test
	public void shouldFailWhenPathIsInvalid() {
		file = new File("xyzcxyaf!?@");
		underTest.retrieveLatest(10);
		assertThat(file.exists(), is(false));
	}

}
