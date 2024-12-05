package com.macys.uop.foundation.core.utils.trace;

import java.util.LinkedHashMap;
import java.util.Map;

import brave.Span;
import brave.Tracer;
import brave.propagation.B3Propagation;
import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Extractor;
import brave.propagation.TraceContext.Injector;

/**
 * Utility interface which provides helper methods related to Brave Tracer
 *
 */
public interface TracerUtil {
	
	/**
	 * Copy tracing headers from existing {@link TraceContext} into {@link Map<String, String>}
	 * 
	 * @param context {@link TraceContext}
	 * 
	 * @return {@link Map<String, String>} containing tracing headers
	 */
	default Map<String, String> injectHeaders(TraceContext context) {
		Map<String, String> headers = new LinkedHashMap<>();
		Injector<Map<String, String>> injector = B3Propagation.FACTORY.get().injector(Map::put);
		injector.inject(context, headers);
		return headers;
	}

	/**
	 * Copy tracing headers from existing {@link TraceContext} into supplied {@link Map<String, String>} 
	 * 
	 * @param context {@link TraceContext} 
	 * @param headers {@link Map<String, String>} 
	 */
	default void injectHeaders(TraceContext context, Map<String, String> headers) {
		Injector<Map<String, String>> injector = B3Propagation.FACTORY.get().injector(Map::put);
		injector.inject(context, headers);
	}

	/**
	 * Constructs a Span from supplied {@link Map<String, String>} headers and name.
	 * <br>
	 * If {@link Map<String, String>} headers does not have propagated information to construct Span then create a new Span with the supplied name from {@link Tracer}  
	 * 
	 * @param tracer {@link Tracer}
	 * @param spanName Span Name
	 * @param headers {@link Map<String, String>} 
	 * @return {@link Span}
	 */
	default Span constructSpan(Tracer tracer, String spanName, Map<String, String> headers) {
		Span span = null;
		Extractor<Map<String, String>> extractor = B3Propagation.FACTORY.get().extractor(Map::get);
		TraceContext context = extractor.extract(headers).context();
		if (context != null) {
			span = tracer.nextSpan(extractor.extract(headers)).name(spanName);
		} else {
			span = tracer.nextSpan().name(spanName);
		}
		return span;
	}
}
