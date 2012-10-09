package com.duckduckgo.mobile.android.objects;

import java.util.HashMap;

import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.util.FileProcessor;

public class SourceProcessor implements FileProcessor {
	
	public SourceProcessor() {
		DDGControlVar.simpleSourceMap = new HashMap<String, String>();
	}

	@Override
	public void processLine(String line) {
		String[] parts = line.split("__");
		DDGControlVar.simpleSourceMap.put(parts[0], parts[1]);
	}

}
