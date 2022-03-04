package com.seele2.encrypt.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SqlHelper {

	private SqlHelper(){}
	// TODO 连表查询时 table 为多个


	private static final Pattern SELECT_FIELD = Pattern.compile(".*select(.+)from.*");

	private static final Pattern SELECT_TABLE = Pattern.compile(".*from(.*)(where)?.*");

	public static void main(String[] args) {
		String str = "SELECT a.*,\n" +
				"\t\t  b.id as b_id,b.jbxx_id as b_jbxxId,b.gx as b_gx,b.xm as b_xm,b.sfzh as b_sfzh,b.del_flag as b_del_flag,\n" +
				"\t\t  c.id as c_id,c.jbxx_id as c_jbxxId,c.gzjl as c_gzjl,c.del_flag as c_del_flag,\n" +
				"\t\t  d.id as d_id,d.jbxx_id as d_jbxxId,d.xxcjjg as d_xxcjjg,d.jdjg as d_jdjg,d.bz as d_bz,d.del_flag as d_del_flag,\n" +
				"\t\t  e.id as e_id,e.jbxx_id as e_jbxxId,e.xm as e_xm,e.sfzh as e_sfzh,e.gx as e_gx,e.del_flag as e_del_flag,\n" +
				"\t\t  f.id as f_id,f.jbxx_id as f_jbxxId,f.cyqk as f_cyqk,f.bzyt as f_bzyt,f.bzsj as f_bzsj,f.blqd as f_blqd,f.del_flag as f_del_flag,\n" +
				"\t\t  g.id as g_id,g.jbxx_id as g_jbxxId,g.xm as g_xm,g.sfzh as g_sfzh,g.gx as g_gx,g.del_flag as g_del_flag,\n" +
				"\t\t  h.id as h_id,h.jbxx_id as h_jbxxId,h.hdtj as h_hdtj,h.hdsj as h_hdsj,h.hdqk as h_hdqk,h.hdgj as h_hdgj,h.del_flag as h_del_flag\n" +
				"\t\tFROM ry_jbxx a\n" +
				"\t\tLEFT JOIN ry_gtshcy b on a.id = b.jbxx_id\n" +
				"\t\tLEFT JOIN ry_gzjl c on a.id = c.jbxx_id\n" +
				"\t\tLEFT JOIN ry_jdxx d on a.id = d.jbxx_id\n" +
				"\t\tLEFT JOIN ry_jtcy e on a.id = e.jbxx_id\n" +
				"\t\tLEFT JOIN ry_more_sfzh f on a.id = f.jbxx_id\n" +
				"\t\tLEFT JOIN ry_shzygxr g on a.id = g.jbxx_id\n" +
				"\t\tLEFT JOIN ry_wjsf h on a.id = h.jbxx_id\n" +
				"\t\tWHERE\n" +
				"\t\t\ta.del_flag = 0\n" +
				"\t\t\tand b.del_flag = 0\n" +
				"\t\t\tand c.del_flag = 0\n" +
				"\t\t\tand d.del_flag = 0\n" +
				"\t\t\tand e.del_flag = 0\n" +
				"\t\t\tand f.del_flag = 0\n" +
				"\t\t\tand g.del_flag = 0\n" +
				"\t\t\tand h.del_flag = 0";

		str = str.replace("\t", " ").replace("\n", " ").toLowerCase();

		Matcher matcher = SELECT_TABLE.matcher(str);

		if (matcher.find()) {
			String group = matcher.group(0);
			System.out.println(group);
			String group1 = matcher.group(1);
			System.out.println(group1);
			String group2 = matcher.group(2);
			System.out.println(group2);
		} else {
			System.out.println("not match!");
		}
	}

	private static String extractField(final String sql) {
		Matcher matcher = SELECT_FIELD.matcher(sql);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			throw new RuntimeException("SQL ERROR: " + sql);
		}
	}

	private static String formatSQL(final String sql) {
		return sql.replace("\t", " ")
				.replace("\r", " ")
				.replace("\n", " ")
				.toLowerCase();
	}
}
