package com.xs.veh.video.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xs.veh.video.entity.VideoConfig;
import com.xs.veh.video.manager.VideoManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


@Controller
@RequestMapping(value = "/video")
public class VideoController {
	
	@Resource(name="videoManager")
	private VideoManager videoManager;
	
	@RequestMapping(value = "play")
	public String getPlayInfo(HttpServletRequest request ,String jylsh) {
		
		List<Map> list = videoManager.getProcessDataByLsh(jylsh);
		
		if(list==null||list.isEmpty()){
			return "video";
		}
		//处理路试
		isRoadTest(list);
		
		String jyjgbh=list.get(0).get("JYJGBH").toString();
		String jcxdh =list.get(0).get("JCXDH").toString();
		
		List<VideoConfig> conifgs = videoManager.getConfig(jyjgbh);
		
		JSONArray ja=new JSONArray();
		
		for(Map item:list){
			String jyxm = (String)item.get("JYXM");
			if(jyxm!=null){
				for(VideoConfig vc:conifgs){
					if(vc.getJyxm().indexOf(jyxm)!=-1&&vc.getJcxdh().equals(jcxdh)){
						JSONObject jo = JSONObject.fromObject(vc);
						Date kssj=(Date)item.get("KSSJ");
						Date jssj=(Date)item.get("JSSJ");
						
						BigDecimal jycs=(BigDecimal)item.get("JYCS");
						
						String fzjg=(String)item.get("FZJG");
						String hphm=(String)item.get("HPHM");
						if(fzjg!=null){
							hphm=fzjg.substring(0, 1)+(String)item.get("HPHM");
						}
						SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						
						jo.put("kssj",sd.format(kssj));
						jo.put("jssj",sd.format(jssj));
						jo.put("hphm",hphm);
						jo.put("jyxm", jyxm);
						jo.put("jycs", jycs.intValue());
						ja.add(jo);
						break;
					}
				}
			}
		}
		request.setAttribute("playInfo", ja.toString());
		return "video";
	}
	
	@RequestMapping(value = "getConfig", method = RequestMethod.POST)
	public @ResponseBody Map  getConfig(String jcjgdh) {
		List list = videoManager.getConfig(jcjgdh);
		Map map =new HashMap();
		map.put("total", list.size());
		map.put("rows", list);
		return map;
	}
	
	@RequestMapping(value = "saveConfig", method = RequestMethod.POST)
	public @ResponseBody String  saveConfig(VideoConfig vc) {
		 videoManager.saveConfig(vc);
		Map<String,Object> map =new HashMap<String,Object>();
		map.put("status", 1);
		map.put("message", "保存成功");
		return JSONObject.fromObject(map).toString();
	}
	
	@RequestMapping(value = "deleteConfig", method = RequestMethod.POST)
	public @ResponseBody String  deleteConfig(VideoConfig vc) {
		 videoManager.deleteConfig(vc);
		Map<String,Object> map =new HashMap<String,Object>();
		map.put("status", 1);
		map.put("message", "删除成功");
		return JSONObject.fromObject(map).toString();
	}
	
	/**
	 * 
	 * @param list
	 */
	private void isRoadTest(List<Map> list){
		
		for(Map map : list){
			String  jcxdh =(String)map.get("JCXDH");
			if(jcxdh==null || "".equals(jcxdh)){
				map.put("JCXDH", "0");
			}
		}
		
	}
	

}
