package com.oracle.iot.model;

public class EventMetric {
	private String metricName;
	private Double eventValue;
	private Double increment;
	private Double alternate;
	private Double loop;
	private Double max;
	private Double min;
	private Boolean hold = Boolean.FALSE;

	public EventMetric(String metricName, Double eventValue, Double increment, Double alternate, Double loop,
			Double max, Double min, Boolean hold) {
		super();
		this.metricName = metricName;
		this.eventValue = eventValue;
		this.increment = increment;
		this.alternate = alternate;
		this.loop = loop;
		this.max = max;
		this.min = min;
		this.hold = hold;
	}

	public String getMetricName() {
		return metricName;
	}

	public Double getEventValue() {
		return eventValue;
	}

	public Double getIncrement() {
		return increment;
	}

	public Double getAlternate() {
		return alternate;
	}

	public Double getLoop() {
		return loop;
	}

	public Double getMax() {
		return max;
	}

	public Double getMin() {
		return min;
	}

	public Boolean getHold() {
		return hold;
	}

}