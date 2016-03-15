package com.home.ratemvc.impls;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.home.ratemvc.interfaces.CurrencyDao;
import com.home.ratemvc.objects.Currency;

@Component("sqliteDAO")
public class SQLiteDAO implements CurrencyDao {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public void insert(Currency currency) {
		String sql = "insert into Currency (code, description) VALUES (?, ?)";
		jdbcTemplate.update(sql, new Object[] { currency.getCode(), currency.getDescription() });
	}

	@Override
	public List<Currency> getCurrencyList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String code) {
		String sql = "delete from Currency where code=?";
		int result = jdbcTemplate.update(sql, code);
	}
	
	public List<Currency> findAll2() {
		String sql = "SELECT * FROM Currency";
		return jdbcTemplate.query(sql, new CurrencyRowMapper());
	}
	
	public List<Currency> findAll(){
		
		String sql = "SELECT * FROM Currency";
			 
		List<Currency> currencies = new ArrayList<Currency>();
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map row : rows) {
			Currency currency = new Currency();
			//customer.setCustId((Long)(row.get("CUST_ID")));
			
			currency.setCode((String)row.get("code"));
			currency.setDescription((String)row.get("description"));
			currencies.add(currency);
		}
			
		return currencies;
	}
	

	private JdbcTemplate getJdbcTemplate() {
		// TODO Auto-generated method stub
		return null;
	}


	private static final class CurrencyRowMapper implements RowMapper<Currency> {

		@Override
		public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
			Currency currency = new Currency();
			//currency.setId(rs.getInt("id"));
			currency.setCode(rs.getString("code"));
			currency.setDescription(rs.getString("description"));
			return currency;                        
		}

	}

	
}
