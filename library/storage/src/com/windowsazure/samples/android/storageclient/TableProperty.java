package com.windowsazure.samples.android.storageclient;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import android.util.Base64;

public class TableProperty<T> {
	
	public static final EdmType DEFAULT_TYPE = EdmType.EdmString;
	public static final String PARTITION_KEY = "PartitionKey";
	public static final String ROW_KEY = "RowKey";
	public static final String TIMESTAMP = "Timestamp";
	public static final String TYPE = "type";
	
	public EdmType getEdmType() {
		return edmType;
	}
	
	public String getName() {
		return name;
	}
	
	public String getRepresentation() {
		return representation;
	}
	
	public T getValue() {
		return value;
	}
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public static TableProperty<?> fromRepresentation(String name, EdmType edmType, String representation)
		throws Exception {
		
		switch (edmType) {
		case EdmBinary:
			byte[] bytes = Base64.decode(representation.getBytes(), Base64.DEFAULT);
			ByteBuffer buffer = ByteBuffer.wrap(bytes);
			return new TableProperty<ByteBuffer>(name, buffer);
		case EdmBoolean:
			boolean booleanValue = Boolean.parseBoolean(representation);
			return new TableProperty<Boolean>(name, booleanValue);
		case EdmDateTime:
			Date date = xmlStringToDate(representation);
			return new TableProperty<Date>(name, date);
		case EdmDouble:
			Double doubleValue = Double.parseDouble(representation);
			return new TableProperty<Double>(name, doubleValue);
		case EdmGuid:
			UUID guid = UUID.fromString(representation);
			return new TableProperty<UUID>(name, guid);
		case EdmInt32:
			Integer int32 = Integer.parseInt(representation);
			return new TableProperty<Integer>(name, int32);
		case EdmInt64:
			Long int64 = Long.parseLong(representation);
			return new TableProperty<Long>(name, int64);
		case EdmString:
			return new TableProperty<String>(name, representation);
		}
		
		throw new Exception("Unable to build Property");
	}
	
	public static TableProperty<ByteBuffer> newProperty(String name, ByteBuffer buffer) {
		return new TableProperty<ByteBuffer>(name, buffer);
	}
	
	public static TableProperty<Boolean> newProperty(String name, boolean booleanValue) {
		return new TableProperty<Boolean>(name, booleanValue);
	}
	
	public static TableProperty<Date> newProperty(String name, Date date) {
		return new TableProperty<Date>(name, date);
	}
	
	public static TableProperty<Double> newProperty(String name, Double doubleValue) {
		return new TableProperty<Double>(name, doubleValue);
	}
	
	public static TableProperty<UUID> newProperty(String name, UUID guid) {
		return new TableProperty<UUID>(name, guid);
	}
	
	public static TableProperty<Integer> newProperty(String name, Integer int32) {
		return new TableProperty<Integer>(name, int32);
	}
	
	public static TableProperty<Long> newProperty(String name, Long int64) {
		return new TableProperty<Long>(name, int64);
	}
	
	public static TableProperty<String> newProperty(String name, String string) {
		return new TableProperty<String>(name, string);
	}
	
	public static TableProperty<?> newProperty(String name, Class<?> type) {		
		return new TableProperty<Object>(name, type);
	}
	
	private TableProperty(String name, T value) {
		this.name = name;
		this.value = value;
		
		if (value instanceof ByteBuffer) {
			this.edmType = EdmType.EdmBinary;
			this.representation = new String(Base64.encode(((ByteBuffer) value).array(), Base64.DEFAULT));
			return;
		}

		if (value instanceof Boolean) {
			this.edmType = EdmType.EdmBoolean;
			this.representation = value.toString();
			return;
		}
		
		if (value instanceof Date) {
			this.edmType = EdmType.EdmDateTime;
			this.representation = dateToXmlStringWithoutTZ((Date) value);
			return;
		}
		
		if (value instanceof Double) {
			this.edmType = EdmType.EdmDouble;
			this.representation = value.toString();
			return;
		}
		
		if (value instanceof UUID) {
			this.edmType = EdmType.EdmGuid;
			this.representation = value.toString();
			return;
		}
		
		if (value instanceof Integer) {
			this.edmType = EdmType.EdmInt32;
			this.representation = value.toString();
			return;
		}
		
		if (value instanceof Long) {
			this.edmType = EdmType.EdmInt64;
			this.representation = value.toString();
			return;
		}
		
		if (value instanceof String) {
			this.edmType = EdmType.EdmString;
			this.representation = (String) value;
			return;
		}
		
		this.edmType = EdmType.EdmUnsupported;
		this.representation = value.toString();
	}
	
	private TableProperty(String name, Class<?> type) {
		this.name = name;		
		
		if (type.equals(ByteBuffer.class)) {
			this.edmType = EdmType.EdmBinary;
			this.representation = "";
			return;
		}
		
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			this.edmType = EdmType.EdmBoolean;
			this.representation = "false";
			return;
		}

		if (type.equals(Date.class)) {
			this.edmType = EdmType.EdmDateTime;
			this.representation = dateToXmlStringWithoutTZ(new Date());
			return;
		}

		if (type.equals(Double.class) || type.equals(double.class)) {
			this.edmType = EdmType.EdmDouble;
			this.representation = "0";
			return;
		}
		
		if (type.equals(UUID.class)) {
			this.edmType = EdmType.EdmGuid;
			this.representation = UUID.randomUUID().toString();
			return;
		}
		
		if (type.equals(Integer.class) || type.equals(int.class)) {
			this.edmType = EdmType.EdmInt32;
			this.representation = "0";
			return;
		}
		
		if (type.equals(Long.class) || type.equals(long.class)) {
			this.edmType = EdmType.EdmInt64;
			this.representation = "0";
			return;
		}
		
		if (type.equals(String.class)) {
			this.edmType = EdmType.EdmString;
			this.representation = UUID.randomUUID().toString();
			return;
		}
		
		this.edmType = EdmType.EdmUnsupported;
	}
	
	private static final String GMT_TZ = "GMT";
	private static final String XML_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	private static String dateToXmlStringWithoutTZ(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(XML_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone(GMT_TZ));
        return sdf.format(date);
	}
	
	private static Date xmlStringToDate(String text) {
		int year = Integer.parseInt(text.substring(0, 4));
		int month = Integer.parseInt(text.substring(5, 7));
		int day = Integer.parseInt(text.substring(8, 10));
		int hour = Integer.parseInt(text.substring(11, 13));
		int minute = Integer.parseInt(text.substring(14, 16));
		int second = Integer.parseInt(text.substring(17, 19));
		Date localDate = new Date(year - 1900, month - 1, day, hour, minute, second);
		return localDateToGmtDate(localDate);
	}

	private static Date localDateToGmtDate(Date date) {
		return new Date(TimeZone.getDefault().getOffset(date.getTime()) + date.getTime());
	}
	
	private EdmType edmType;
	private String name;
	private String representation;
	private T value;
	
}
