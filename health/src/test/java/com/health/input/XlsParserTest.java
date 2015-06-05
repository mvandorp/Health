package com.health.input;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.health.Column;
import com.health.Table;
import com.health.ValueType;

public class XlsParserTest {
private XlsParser xp;
	
	@Before
	public void setUp() { 
		xp = new XlsParser();
	}
	
	@Test
	public void parse_columns_correct() {
		//InputDescriptor id = mock(InputDescriptor.class);
		
		Table actual = null;
		try {
			InputDescriptor id = new InputDescriptor("data/configXmls/admireXlsConfig.xml");
	        String xlsPath = "data/data_all/data_xls/ADMIRE_56_BPM.xls";
	        
	        actual = xp.parse(xlsPath, id);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
        assertNotNull(actual);
        
        List<Column> actualColumns = actual.getColumns();
        String[] expectedNames = {"date", "time", "sys", "dia", "puls"};
        ValueType[] expectedTypes = {ValueType.Date, ValueType.String, ValueType.Number, ValueType.Number, ValueType.Number};
        for(int i = 0; i < expectedTypes.length; i++) {
        	Column col = actualColumns.get(i);
        	assertEquals(expectedNames[i], col.getName());
        	assertEquals(expectedTypes[i], col.getType());
        }
    }

}