package com.macys.uop.foundation.core.utils.task.support;

import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

import com.google.cloud.spanner.Key;

public interface EventStateRepository extends SpannerRepository<EventState, Key> {

}
