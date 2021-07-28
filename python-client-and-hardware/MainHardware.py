import NetClient

PORT = 8080
IP = raw_input('Input ip to connect to:')

net_client = NetClient.NetClient(IP)
net_client.start()

choice = 0

while choice != 2:
    choice = input("(1) Send String, (2) Exit")

    if choice == 1:
        print("Sent String")
        net_client.send_string("This is from the mat")
    else:
        if choice == 2:
            print("Exit")
            net_client.close()
        else:
            print("Incorrect input, try again.")
