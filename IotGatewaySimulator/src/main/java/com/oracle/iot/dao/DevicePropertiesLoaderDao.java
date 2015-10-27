package com.oracle.iot.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.oracle.iot.model.Constants;
import com.oracle.iot.model.PropertyDeviceDetails;
import com.oracle.iot.model.PropertyMetric;

@Repository
public class DevicePropertiesLoaderDao {

	private static final Logger logger = Logger.getLogger(DevicePropertiesLoaderDao.class);

	Map<String, PropertyDeviceDetails> devices = new LinkedHashMap<String, PropertyDeviceDetails>();

	public DevicePropertiesLoaderDao() {
		super();
		loadDevices();
	}

	public Boolean loadNewDevice(MultipartFile multipartFile) {
		try {
			Properties prop = new Properties();
			prop.load(multipartFile.getInputStream());
			String name = prop.getProperty("name");
			PropertyDeviceDetails newDevice = extractDeviceFromProperties(prop, name);
			devices.put(name, newDevice);
			return true;
		} catch (Exception e) {
			logger.error("Something went wrong with your file", e);
			return false;
		}
	}

	private void loadDevices() {
		try {
			InputStream stream = this.getClass().getClassLoader().getResourceAsStream("devices/index.properties");
			if (stream == null) {
				logger.error(
						"Could not load the properties directory index for some reason...maybe it spontaneously combusted!!");
				return;
			}
			Properties index = new Properties();
			index.load(stream);

			for (String filename : index.getProperty("files").split(",")) {
				Properties prop = new Properties();
				stream = this.getClass().getClassLoader().getResourceAsStream("devices/" + filename);
				if (stream == null) {
					logger.error("Could not load the properties file for some reason...check your spelling");
					return;
				}
				prop.load(stream);

				// load device specific details
				String name = Constants.removeWhiteSpace(prop.getProperty("name"));
				PropertyDeviceDetails newDevice = extractDeviceFromProperties(prop, name);
				devices.put(name, newDevice);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private PropertyDeviceDetails extractDeviceFromProperties(Properties prop, String name) {
		String deviceName = prop.getProperty("display.name");
		String dataFormat = Constants.removeWhiteSpace(prop.getProperty("data.format"));
		String alertFormat = Constants.removeWhiteSpace(prop.getProperty("alert.format"));
		String picture = prop.getProperty("picture", "widget.png");
		PropertyDeviceDetails newDevice = new PropertyDeviceDetails(name, deviceName, dataFormat, alertFormat, picture);

		// load metrics
		List<String> metrics = Constants.removeWhiteSpace(Arrays.asList(prop.getProperty("metrics").split(",")));
		for (String metric : metrics) {
			String prefix = "metrics.";
			String displayName = prop.getProperty(prefix + metric + ".display");
			String flag = prop.getProperty(prefix + metric + ".boolean");
			if (flag != null) {
				Boolean boolSet = flag.equalsIgnoreCase("true");
				newDevice.addMetric(metric, displayName, boolSet);
			} else {
				Double defaultValue = Double.valueOf(prop.getProperty(prefix + metric + ".default"));
				Double increment = Constants.doubleOrNull(prop.getProperty(prefix + metric + ".increment"));
				Double alternate = Constants.doubleOrNull(prop.getProperty(prefix + metric + ".alternate"));
				Double loop = Constants.doubleOrNull(prop.getProperty(prefix + metric + ".loop"));
				Double max = Constants.doubleOrNull(prop.getProperty(prefix + metric + ".max"));
				Double min = Constants.doubleOrNull(prop.getProperty(prefix + metric + ".min"));
				newDevice.addMetric(metric, displayName, defaultValue, increment, alternate, loop, max, min);
			}
		}

		// load alerts
		List<String> alerts = Constants.removeWhiteSpace(Arrays.asList(prop.getProperty("alerts").split(",")));
		for (String alert : alerts) {
			String displayName = prop.getProperty("alerts." + alert + ".display");
			newDevice.addAlert(alert, displayName);
		}

		// load events
		List<String> events = Arrays.asList(prop.getProperty("events").split(","));
		for (String event : events) {
			String prefix = "events.";
			String displayName = prop.getProperty(prefix + event + ".display");
			Integer priority = Integer.valueOf(prop.getProperty(prefix + event + ".priority", "1"));
			for (PropertyMetric eventMetric : newDevice.getMetrics()) {
				String metricName = eventMetric.getName();
				String eventMetricBase = prefix + event + "." + metricName;
				String flag = prop.getProperty(eventMetricBase + ".boolean");
				if (flag != null) {
					Boolean boolSet = Boolean.getBoolean(flag.toLowerCase());
					newDevice.addEvent(event, displayName, priority, metricName, boolSet);
				} else {
					Double value = Constants.doubleOrNull(prop.getProperty(eventMetricBase + ".value"));
					Double increment = Constants.doubleOrNull(prop.getProperty(eventMetricBase + ".increment"));
					Double loop = Constants.doubleOrNull(prop.getProperty(eventMetricBase + ".loop"));
					Boolean hold = prop.getProperty(eventMetricBase + ".hold", "false").equalsIgnoreCase("true");
					if (value != null || increment != null || loop != null || hold) {
						Double alternate = Constants.doubleOrNull(prop.getProperty(eventMetricBase + ".alternate"));
						Double max = Constants.doubleOrNull(prop.getProperty(eventMetricBase + ".max"));
						Double min = Constants.doubleOrNull(prop.getProperty(eventMetricBase + ".min"));
						newDevice.addEvent(event, displayName, priority, metricName, value, increment, alternate, loop,
								max, min, hold);
					}
				}
			}
		}
		return newDevice;
	}

	public List<String> getDeviceNames() {
		List<String> list = new ArrayList<String>(devices.keySet());
		Collections.sort(list, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		return list;
	}

	public PropertyDeviceDetails getDevice(String name) {
		return devices.get(name);
	}

	public List<Map<String, Object>> getTypes(boolean all) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (String name : devices.keySet()) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("name", name);
			map.put("display", devices.get(name).getDisplayName());
			map.put("enabled", devices.get(name).getEnabled());
			// only add if we want all or it is an enabled device
			if (all || devices.get(name).getEnabled()) {
				list.add(map);
			}
		}
		Collections.sort(list, new Comparator<Map<String, Object>>() {

			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String display1 = (String) o1.get("display");
				String display2 = (String) o2.get("display");
				return display1.compareToIgnoreCase(display2);
			}

		});
		return list;
	}

}
