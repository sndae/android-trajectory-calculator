
import sys,subprocess
from math import *
#The file passed by argument
input_file=open(sys.argv[1],'r')
file_name=sys.argv[1].split(".")[0]
velocities_file="./velocities/"+file_name+".txt"
trajectory_file="./trajectories/"+file_name+".txt"
#Output strings
last_accel_time=0.0
last_accel=[0.0,0.0,0.0]
last_rota=[0.0,0.0,0.0]
clean=""
last_rota_time=0.0

NANOINSEC=1000000000

def normalize_movement(rotation,movement):
	aX,aY,aZ=rotation
	x,y,z=movement
	#despues de rot x = x, y*cos(a)+z*sin(a), -y*sen(a)+z*cos(a)
	x,y,z=[x,y*cos(aX)+z*sin(aX),-y*sin(aX)+z*cos(aX)]
	#rot y=x*cos(b),y,x*sin(b)+z*cos(b)
	x,y,z=[x*cos(aY)-z*sin(aY),y,x*sin(aY)+z*cos(aY)]
	#rpt z=x*cos(c)+y*sen(c),-x*sin(c)+y*cos(c),z
	x,y,z=[x*cos(aZ)+y*sin(aZ),-x*sin(aZ)+y*cos(aZ),z]
	movement=x,y,z
	return movement
i=0
#Turns the lineal acc into lineal vel, and standarize the format of the rotational vel
for line in input_file:
	i=i+1
	first_split=line.split(",",1)
	second_split=first_split[1].split(",")
	current_time=float(second_split[0])/NANOINSEC
	if (first_split[0]=="GYR"):		
		delta_time=current_time-last_rota_time
		#Lectura repetida
		if(delta_time!=0.0):
			if(last_rota_time==0.0):
				delta_time=0

			clean+="r,"+str(current_time)+","+str(delta_time)+","+str(last_rota[0])+","+str(last_rota[1])+","+str(last_rota[2])+"\n"
			print i
			#ojo!
			#if(i!=1819 and i!=6668):
			last_rota=[float(second_split[1]),float(second_split[2]),float(second_split[3])]
			last_rota_time=current_time
		
	if(first_split[0]=="NACC"):
		#The actual velocity is calculated using the last acceleration info
		delta_time=current_time-last_accel_time
		#Lectura repetida
		if(delta_time!=0.0):
			if(last_accel_time!=0.0):				
				clean+="l,"+str(last_accel_time)+","+str(delta_time)+","+str(delta_time*last_accel[0])+","+str(delta_time*last_accel[1])+","+str(delta_time*last_accel[2])+"\n"				
				last_accel=[float(second_split[1]),float(second_split[2]),float(second_split[3])]
			last_accel_time=current_time
		
input_file.close()


output_file=open(velocities_file,'w')
output_file.write(clean)
output_file.close()

#Sorts the velocity file
output_file=open(velocities_file.split(".")[0]+"_sorted.txt",'w')
print subprocess.call(["sort", "-k2","-t,",velocities_file], stdout = output_file)
output_file.close()

#Reads the file generated before and turns it into a trajectory
input_file=open(velocities_file.split(".")[0]+"_sorted.txt",'r')
clean=""
current_rotation=[0.0,0.0,0.0]
current_position=[0.0,0.0,0.0]
first_time=0.0
i=0
for line in input_file:
	i=i+1
	split=line.split(",")
	current_time=float(split[1])
	duration=float(split[2])
	if(first_time==0.0):
		first_time=current_time
	relative_time=current_time-first_time
	
#	print str(last_time)+","+str(current_time)+","+str(relative_time)
	#Only calcules if duration != 0
	if(duration!=0.0):
		if(split[0]=="r"):
			delta_rotation=[float(split[3])*duration,float(split[4])*duration,float(split[5])*duration]
			current_rotation=[current_rotation[0]+delta_rotation[0],current_rotation[1]+delta_rotation[1],current_rotation[2]+delta_rotation[2]]
		if(split[0]=="l"):
			delta_position=[float(split[3])*duration,float(split[4])*duration,float(split[5])*duration]
			print delta_position
			delta_position=normalize_movement(current_rotation,delta_position)
			print delta_position
			current_position=[current_position[0]+delta_position[0],current_position[1]+delta_position[1],current_position[2]+delta_position[2]]
		#Outputs as: current_time,x,y,z,pitch,roll,azimuth
		if(i%100==0):
			clean+=str(relative_time)+","+str(current_position[0])+","+str(current_position[1])+","+str(current_position[2])+","+str(current_rotation[0])+","+str(current_rotation[1])+","+str(current_rotation[2])+"\n"
	last_time=current_time
output_file=open(trajectory_file,'w')
output_file.write(clean)
output_file.close()


