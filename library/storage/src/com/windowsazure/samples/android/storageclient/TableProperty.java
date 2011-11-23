package com.windowsazure.samples.android.storageclient;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import android.util.Base64;

final class TableProperty<T> {
	
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
			return new TableProperty<ByteBuffer>(name, ByteBuffer.class, buffer);
		case EdmBoolean:
			boolean booleanValue = Boolean.parseBoolean(representation);
			return new TableProperty<Boolean>(name, Boolean.class, booleanValue);
		case EdmDateTime:
			Date date = xmlStringToDate(representation);
			return new TableProperty<Date>(name, Date.class, date);
		case EdmDouble:
			Double doubleValue = Double.parseDouble(representation);
			return new TableProperty<Double>(name, Double.class, doubleValue);
		case EdmGuid:
			UUID guid = UUID.fromString(representation);
			return new TableProperty<UUID>(name, UUID.class, guid);
		case EdmInt32:
			Integer int32 = Integer.parseInt(representation);
			return new TableProperty<Integer>(name, Integer.class, int32);
		case EdmInt64:
			Long int64 = Long.parseLong(representation);
			return new TableProperty<Long>(name, Long.class, int64);
		case EdmString:
			return new TableProperty<String>(name, String.class, representation);
		}
		
		throw new Exception("Unable to build Property");
	}
	
	public static TableProperty<ByteBuffer> newProperty(String name, ByteBuffer buffer) {
		return new TableProperty<ByteBuffer>(name, ByteBuffer.class, buffer);
	}
	
	public static TableProperty<Boolean> newProperty(String name, boolean booleanValue) {
		return new TableProperty<Boolean>(name, Boolean.class, booleanValue);
	}
	
	public static TableProperty<Date> newProperty(String name, Date date) {
		return new TableProperty<Date>(name, Date.class, date);
	}
	
	public static TableProperty<Double> newProperty(String name, Double doubleValue) {
		return new TableProperty<Double>(name, Double.class, doubleValue);
	}
	
	public static TableProperty<UUID> newProperty(String name, UUID guid) {
		return new TableProperty<UUID>(name, UUID.class, guid);
	}
	
	public static TableProperty<Integer> newProperty(String name, Integer int32) {
		return new TableProperty<Integer>(name, Integer.class, int32);
	}
	
	public static TableProperty<Long> newProperty(String name, Long int64) {
		return new TableProperty<Long>(name, Long.class, int64);
	}
	
	public static TableProperty<String> newProperty(String name, String string) {
		return new TableProperty<String>(name, String.class, string);
	}
	
	public static TableProperty<?> newProperty(String name, Class<?> type) {				
		TableProperty<?> result = null;
		
		if (type.equals(ByteBuffer.class)) {
			result = newProperty(name, (ByteBuffer)null);
		}
		
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			result = newProperty(name, false);
		}

		if (type.equals(Date.class)) {
			result = newProperty(name, new Date());
		}

		if (type.equals(Double.class) || type.equals(double.class)) {
			result = newProperty(name, 0.0);
		}
		
		if (type.equals(UUID.class)) {
			result = newProperty(name, UUID.randomUUID());
		}
		
		if (type.equals(Integer.class) || type.equals(int.class)) {
			result = newProperty(name, 0);
		}
		
		if (type.equals(Long.class) || type.equals(long.class)) {
			result = newProperty(name, 0l);
		}
		
		if (type.equals(String.class)) {
			result = newProperty(name, UUID.randomUUID().toString());
		}
		
		return result;
	}
	
	public static TableProperty<?> newProperty(String name, Class<?> type, Object value) {
		TableProperty<?> result = null;
		
		if (type.equals(ByteBuffer.class)) {
			result = newProperty(name, (ByteBuffer)value);
		}
		
		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			result = newProperty(name, (Boolean)value);
		}

		if (type.equals(Date.class)) {
			result = newProperty(name, (Date)value);
		}

		if (type.equals(Double.class) || type.equals(double.class)) {
			result = newProperty(name, (Double)value);
		}
		
		if (type.equals(UUID.class)) {
			result = newProperty(name, (UUID)value);
		}
		
		if (type.equals(Integer.class) || type.equals(int.class)) {
			result = newProperty(name, (Integer)value);
		}
		
		if (type.equals(Long.class) || type.equals(long.class)) {
			result = newProperty(name, (Long)value);
		}
		
		if (type.equals(String.class)) {
			result = newProperty(name, (String)value);
		}
		
		return result;
	}
	
	private TableProperty(String name, Class<T> type, T value) {
		this.name = name;
		this.value = value;
		
		if (type.equals(ByteBuffer.class)) {
			this.edmType = EdmType.EdmBinary;
			this.representation = new String(Base64.encode(((ByteBuffer) value).array(), Base64.DEFAULT));
			return;
		}

		if (type.equals(Boolean.class) || type.equals(boolean.class)) {
			this.edmType = EdmType.EdmBoolean;
			this.representation = value.toString();
			return;
		}
		
		if (type.equals(Double.class) || type.equals(double.class)) {
			this.edmType = EdmType.EdmDateTime;
			this.representation = dateToXmlStringWithoutTZ((Date) value);
			return;
		}
		
		if (type.equals(Double.class) || type.equals(double.class)) {
			this.edmType = EdmType.EdmDouble;
			this.representation = value.toString();
			return;
		}
		
		if (type.equals(UUID.class)) {
			this.edmType = EdmType.EdmGuid;
			this.representation = value.toString();
			return;
		}
		
		if (type.equals(Integer.class) || type.equals(int.class)) {
			this.edmType = EdmType.EdmInt32;
			this.representation = value.toString();
			return;
		}
		
		if (type.equals(Long.class) || type.equals(long.class)) {
			this.edmType = EdmType.EdmInt64;
			this.representation = value.toString();
			return;
		}
		
		if (type.equals(String.class)) {
			this.edmType = EdmType.EdmString;
			this.representation = (String) value;
			return;
		}
		
		this.edmType = EdmType.EdmUnsupported;
		if (value != null) this.representation = value.toString();
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
