package com.home.ratemvc.impls;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.util.List;
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
		String sql = "SELECT * FROM Currency";
		return jdbcTemplate.query(sql, new CurrencyRowMapper());
	}

	@Override
	public void delete(String code) {
		String sql = "delete from Currency where code=?";
		jdbcTemplate.update(sql, code);
	}
	
	private static final class CurrencyRowMapper implements RowMapper<Currency> {

		@Override
		public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
			Currency currency = new Currency();
			currency.setCode(rs.getString("code"));
			currency.setDescription(rs.getString("description"));
			return currency;                        
		}

	}

	
}
