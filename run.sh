#!/bin/bash


echo "\n****************** L O C K I N G   S C H E M E *********************"
echo "*                                                                    *"
echo "* We are executing locking scheme to protect the shared file using   *"
echo "* Token Ring Algorithm                                               *"
echo "*                                                                    *"
echo "**********************************************************************"

echo "\nEnter the no. of processes:"
read n


process_id=$((n - 1))
for var in $(seq 0 $process_id)
do
   printf "P$var --> "
done
printf "P0"
printf "\n"

for var in $(seq 0 $process_id)
do
   gnome-terminal -- java -jar process.jar $n $var &
done
wait

