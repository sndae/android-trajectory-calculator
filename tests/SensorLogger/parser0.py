#turns the raw file from the phone into a clean file conteining only gyroscope an net acceleration info, with 
#the absolut timestamp of the sensors converted to represent the time since the last reading of a sensor. 

import sys

input_file=open(sys.argv[1],'r')
clean_acceleration=""
clean_rotation=""
last_accel=0
last_rota=0
for line in input_file:
	first_split=line.split(":")
	second_split=first_split[1].split(",",1)
	if (first_split[0]=="GYR"):
		#First line of the archive
		if(last_rota==0):
			clean_rotation+="0,"+second_split[1]	
		else:
			clean_rotation+=str(int(second_split[0])-last_rota)+","+second_split[1]
		last_rota=int(second_split[0])
	if(first_split[0]=="NACC"):
		#First line of the archive
		if(last_accel==0):
			clean_acceleration+="0,"+second_split[1]
		else:
			clean_acceleration+=str(int(second_split[0])-last_accel)+","+second_split[1]
		last_accel=int(second_split[0])		
input_file.close()

output_file=open("rot_"+sys.argv[1],'w')
output_file.write(clean_rotation)
output_file.close()

output_file=open("acc_"+sys.argv[1],'w')
output_file.write(clean_acceleration)
output_file.close()
	
	 