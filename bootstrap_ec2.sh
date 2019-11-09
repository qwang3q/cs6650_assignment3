# ip of vms
vms=("34.221.22.25"
"52.33.60.108"
"34.222.99.27"
"54.186.137.111"
)

for vm in "${vms[@]}"; do
	if [[ "$1" == "--install" ]]; then
		ssh -i ~/.ssh/cs6650_oregon.pem -t ec2-user@${vm} '
			sudo yum install java-1.8.0
			sudo yum remove java-1.7.0-openjdk
			sudo yum install tomcat8 tomcat8-webapps
			sudo service tomcat8 start
			sudo chmod 777 /usr/share/tomcat8/webapps
		'
	fi
 
	scp -i ~/.ssh/cs6650_oregon.pem  /Users/qianwang/course/cs6650/assignments/assignment2/cs6650_assignment2/out/artifacts/assignment2_war/assignment2_war.war  ec2-user@${vm}:/usr/share/tomcat8/webapps
	ssh -i ~/.ssh/cs6650_oregon.pem -t ec2-user@${vm} '
		sudo service tomcat8 stop
		sudo service tomcat8 start
	'
done


