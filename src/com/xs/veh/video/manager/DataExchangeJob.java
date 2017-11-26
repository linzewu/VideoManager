package com.xs.veh.video.manager;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.xs.veh.video.entity.VideoConfig;
import com.xs.veh.video.entity.VideoDowloadState;
import com.xs.veh.video.entity.VideoDowloadStatePK;
import com.xs.veh.video.mssql.entity.VideoInfo;

@Component("dataExchangeJob")
public class DataExchangeJob {

	private static Logger logger = Logger.getLogger(DataExchangeJob.class);

	@Autowired
	private SessionFactory sessionFactory;

	@Resource(name = "hibernateTemplate")
	private HibernateTemplate hibernateTemplate;

	@Resource(name = "hibernateTemplate2")
	private HibernateTemplate hibernateTemplate2;

	@PostConstruct
	public void intJob() {
	}

	/**
	 * 凌晨一点清理掉交换目录的数据
	 */
	// @Scheduled(cron = "0 0 3 * * ? ")
	public void emptyDataFile() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String rq = sdf.format(new Date());

		String tomcatPath = this.getClass().getClassLoader().getResource("/").getPath();

		tomcatPath = tomcatPath.substring(0, tomcatPath.indexOf("/webapps"));

		String dataResPath = tomcatPath + "/DataRes/";

		delAllFile(dataResPath);

	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	//@Scheduled(fixedDelay = 1000 * 60)
	public void scanVideo() {
		List data = this.hibernateTemplate.execute(new HibernateCallback<List>() {
			@Override
			public List doInHibernate(Session session) throws HibernateException {
				String sql = "select t1.* from veh_is_procstatus t1 left join VIDEO_DOWLOAD_STATE t2 on t1.lsh=t2.lsh and t1.jyxm=t2.jyxm and t1.jycs=t2.jycs and t1.JLZTGXSJ=t2.JLZTGXSJ where t1.jyxm!='DC' and t1.jyxm!='A1' and t1.jyxm!='00' and  t1.jyzt='2' and t1.jlzt='1' and t1.kssj is not null and t1.jssj  is not null and t1.kssj>to_date(to_char(sysdate,'YYYY-MM-dd'),'yyyy-mm-dd') and t2.xzzt is null and t1.jcxdh is not null";
				return session.createSQLQuery(sql).setFirstResult(0).setMaxResults(100)
						.setResultTransformer(DetachedCriteria.ALIAS_TO_ENTITY_MAP).list();
			}
		});

		List<VideoConfig> configs = (List<VideoConfig>) this.hibernateTemplate.find("from VideoConfig");

		for (Object o : data) {
			Map map = (Map) o;
			String lsh = (String) map.get("LSH");
			String jylsh = (String) map.get("JYLSH");
			String jyjgbh = (String) map.get("JYJGBH");
			String jyjgmc = (String) map.get("JYJGMC");
			String jcxdh = (String) map.get("JCXDH");
			String hphm = (String) map.get("HPHM");
			String hpzl = (String) map.get("HPZL");
			String clsbdh = (String) map.get("CLSBDH");
			String jyxm = (String) map.get("JYXM");
			BigDecimal jycs = (BigDecimal) map.get("JYCS");
			String jylb = (String) map.get("JYLB");
			Date kssj = (Date) map.get("KSSJ");
			Date jssj = (Date) map.get("JSSJ");
			Date jlztgxsj = (Date) map.get("JLZTGXSJ");
			VideoConfig c = null;
			for (VideoConfig config : configs) {
				if (config.getJyjgbh().equals(jyjgbh) && config.getJcxdh().equals(jcxdh)
						&& config.getJyxm().contains(jyxm) ) {
					c = config;
					break;
				}
			}
			VideoInfo v = new VideoInfo();
			v.setJyjgbh(jyjgbh);
			v.setJylsh(jylsh);
			v.setHphm(hphm);
			v.setJcxdh(Integer.parseInt(jcxdh));
			v.setHphm(hphm);
			v.setHpzl(hpzl);
			v.setJyxm(jyxm);
			v.setClsbdh(clsbdh);
			v.setKssj(kssj);
			v.setJssj(jssj);
			v.setJylb(jylb);
			v.setJycs(jycs.intValue());
			if (c != null) {
				v.setIp(c.getIp());
				v.setPort(c.getPort());
				v.setPassword(c.getPassword());
				v.setUserName(c.getUserName());
				v.setChannelno(String.valueOf(c.getChannel()));
			}
			this.hibernateTemplate2.save(v);

			VideoDowloadStatePK pk = new VideoDowloadStatePK();
			pk.setJlztgxsj(jlztgxsj);
			pk.setJycs(jycs.intValue());
			pk.setJyxm(jyxm);
			pk.setLsh(lsh);

			VideoDowloadState videoDowloadState = new VideoDowloadState();
			videoDowloadState.setPk(pk);
			videoDowloadState.setXzzt("1");

			this.hibernateTemplate.save(videoDowloadState);

		}

	}

}
