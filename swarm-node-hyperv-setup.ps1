# Swarm mode using Docker Machine

$managers=3
$workers=3

# Change the SwitchName to hte name of your virtual switch
$SwitchName = "New Virtual Switch"

# create manager machines
echo "==========> Creating manager machines...."
for ($node=1;$node -le $managers; $node++) {
    echo "=======> creating manager$node machine ...."
    docker-machine create -d hyperv --hyperv-virtual-switch $SwitchName ('manager' + $node)
}

# create worker machines
echo "=======> Creating worker machines ...."
for ($node=1; $node -le $workers; $node++) {
    echo "====> Creating worker$node machine ..."
    docker-machine create -d hyperv --hyperv-virtual-switch $SwitchName ('worker'+$node)
}

#