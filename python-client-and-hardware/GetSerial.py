from threading import Thread
import serial

class GetSerial(Thread):
    def __init__(self, net_client, num):
        super(GetSerial, self).__init__()
        self.net_client = net_client
        self.id = num

    def run(self):
        
        ser = serial.Serial('/dev/ttyACM0', 38400)
        while 1:
            self.net_client.send_string(self.id + " " + ser.readline())





