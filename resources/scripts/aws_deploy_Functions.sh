#!/bin/sh
DistributionName=$(awk -F= '/^NAME/{print $2}' /etc/os-release)
if [ "$DistributionName" = '"Red Hat Enterprise Linux Server"' ]; then 
echo "Distribution is RHEL"
rpm --import https://packages.microsoft.com/keys/microsoft.asc
sh -c 'echo -e "[azure-cli]\nname=Azure CLI\nbaseurl=https://packages.microsoft.com/yumrepos/azure-cli\nenabled=1\ngpgcheck=1\ngpgkey=https://packages.microsoft.com/keys/microsoft.asc" > /etc/yum.repos.d/azure-cli.repo'
yum install -y azure-cli
elif [[ "$DistributionName" = '"Ubuntu"' ] || [ "$DistributionName" = '"Debian GNU/Linux"' ]]; then
echo "Distribution is UBUNTU"
apt-get install apt-transport-https lsb-release software-properties-common dirmngr -y
AZ_REPO=$(lsb_release -cs)
                      echo "deb [arch=amd64] https://packages.microsoft.com/repos/azure-cli/ $AZ_REPO main" | \
                      tee /etc/apt/sources.list.d/azure-cli.list
apt-key --keyring /etc/apt/trusted.gpg.d/Microsoft.gpg adv \
                          --keyserver packages.microsoft.com \
                          --recv-keys BC528686B50D79E339D3721CEB3E94ADBE1229CF 
apt-get update -y 
apt-get install azure-cli -y
else
echo "[ERROR] The OS [$DistributionName] is currently not supported by the global library for azure CLI installation. Please make use of the container build option [containerBuild:true] with the respective commands. See documentation for details"
exit 1
fi
