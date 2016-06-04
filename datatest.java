
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class datatest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String datafile, nodemap, predictmap, noisyrate, nodedir;
		datafile = args[0];
		nodemap = args[1];
		predictmap = args[2];
		noisyrate = args[3];
		Double rate = Double.parseDouble(noisyrate);
		nodedir = args[4];
		//ֱ�ӻ������Ҫ�ĸ�������
		
		
		BufferedReader br1 = read(nodemap);	
		Map<String, String> map = new HashMap<String, String>();
		map = transform1(br1);
		//����subject,object��ӳ���ļ����������map
		
		
		BufferedReader br2 = read(predictmap);	
		Map<String, String> map2 = new HashMap<String, String>();
		map2 = transform2(br2);
        //����predict��ӳ���ļ����������map2
		
       
	    File dir = null;
		dir = new File(nodedir);
		File[] files = dir.listFiles();
		//���Ŀ¼�µ������ļ�
		List<File> filesList = new ArrayList<File>();
		for(File f:files){
			filesList.add(f);
		}
		int filesNum = filesList.size();
		//�ļ�����
		
       
        String reg1 = "^e";
    	String reg2 = "^%Cand";
    	String reg3 = "^[0-9]";
    	Pattern p1 = Pattern.compile(reg1);
    	Pattern p2 = Pattern.compile(reg2);
    	Pattern p3 = Pattern.compile(reg3);
    	//��������ʽ��ʾ����Ϣ��ƥ��
    	
    	
    	Map<String, ArrayList> maps = new HashMap<String, ArrayList>();
    	//׼���洢��%Cand�����Ϊ��ֵ���������ֵĶ��ֵ��ɵļ���Ϊֵ��maps

		
    	BufferedWriter bw = null;
		bw = write();
    	for(int count = 0; count < filesNum; count++){//��Ŀ¼�µ�ÿ���ļ����ν��д���
    		maps.clear();
    		//ÿ���ļ��������֮��maps���
    		BufferedReader br3 = read(nodedir+"/"+filesList.get(count).getName());
            //��������ļ�,read������Ϊ�ļ���
        	String num = null;
        	//%Cand���ֵ
        	String line = "";
        	
            try {
            	line = br3.readLine();        	
            	while(line != null){
            		Matcher m2 = p2.matcher(line);
            		if(!m2.find()){
    					line = br3.readLine();
    				}
            		else{
    					num = null;
    					String[] tmp = line.split("\\s+");
    					num = tmp[1];
    					
    					ArrayList list = new ArrayList();
    					
    					while((line=br3.readLine()) != null){
    						//��û�е��ļ�ĩβ��Ҳû������һ��Candʱ�ͼ���������ֵ��ӵ�list			
    						Matcher m3 = p3.matcher(line);
    						if(m3.find()){
    							list.add(line);
    						}
    						m2 = p2.matcher(line);
    						if(m2.find()){
    							break;
    						}
    					}
    									
    					maps.put(num, list);
    					
    				}
    							
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            //���ˣ���������������ѯ�ļ���maps������ϣ�������������һ�ζԲ�ѯ�ļ��Ķ�ȡ   

			
            BufferedReader again = read(nodedir+"/"+filesList.get(count).getName());
    		String data = "";
    		try {
    			while((data = again.readLine()) != null){
    				
    				Matcher m1 = p1.matcher(data);
    				if(m1.find()){
    					String subject = null;
    					String object = null;
    					String predict = null;
    					String[] tmp1 = data.split("\\s+");
    					subject = tmp1[1];
    					predict = tmp1[2];
    					object = tmp1[3];
    					String predictOut = map2.get(predict);
    	
    					    					
    					int ans = map.size();
    					ArrayList mapArr = new ArrayList();
    					int index = 0;
    					for(String str:map.keySet()){
    						String res = map.get(str);
    						mapArr.add(res);
    						index++;
    					}
    					for(String str:maps.keySet()){
    						
    						ArrayList arr = new ArrayList();
    						arr = maps.get(str);
    						int sum = arr.size();
    						if(str.equals(subject)){
    							
    							for(int i = 0; i < sum; i++){
    								if(Math.random() <= rate){
    									String subOut = map.get(arr.get(i));
    									int randNum = (int) (Math.random() * ans);
    									String objOut = (String) mapArr.get(randNum);	
    									//object���������map����ȡ��
    									String end = subOut + "\t" + predictOut + "\t" + objOut;
    									bw.write(end);
    									bw.newLine();
    								}						
    							}						
    							
    						}else if(str.equals(object)){
    							
    							for(int i = 0; i < sum; i++){
    								if(Math.random() <= rate){
    									String objOut = map.get(arr.get(i));
    									int randNum = (int) (Math.random() * ans);
    									String subOut = (String) mapArr.get(randNum);
    									//subject���������map����ȡ��
    									String end = subOut + "\t" + predictOut + "\t" + objOut;
    									bw.write(end);
    									bw.newLine();
    								}	
    							}
    							
    						}
    					}
    					
    				}else {			
    				}
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    	}
    	try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	}
    	   
        
	
	static BufferedReader read(String fileName){
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStreamReader iReader = null;
		try {
			iReader = new InputStreamReader(inputStream, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(iReader);
		return br;
	}
	
		
	static Map<String, String> transform1(BufferedReader br){
		Map<String, String> map = new HashMap<String, String>();
		String data = "";
		String key = "", value = "";
		try {
			while((data = br.readLine()) != null){
				int position = data.indexOf("	");
				//�ҵ���һ�γ����Ʊ���ĵط���Ȼ��ǰ������ִ��������ַ������зָ�
				key = data.substring(0, position);
				value = data.substring(position+1).trim();
				map.put(key, value);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	
	static Map<String, String> transform2(BufferedReader br){
		Map<String, String> map = new HashMap<String, String>();
		String data = "";
		String key = "", value = "";
		try {
			while((data = br.readLine()) != null){
				String[] temp = data.split("\\s+");
				key = temp[1];
				value = temp[0];
				map.put(key, value);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	
	
	static BufferedWriter write(){
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream("./output.dat");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		OutputStreamWriter oWriter = new OutputStreamWriter(fileOutputStream);
		BufferedWriter bw = new BufferedWriter(oWriter);
		return bw;
		
	}
	
	
}


