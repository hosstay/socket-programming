from threading import Thread
import socket
import GetSerial

class NetClient(Thread):
    identity = ""
    def __init__(self, ip):
        super(NetClient, self).__init__()
        self.IP = ip
        self.PORT = 8080
        self.identity = 'hardware~'
        self.id = '0'
        self.send_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

    def run(self):
        # connect socket to server
        self.send_socket.connect((self.IP, self.PORT))

        # setup server socket to listen for connections
        self.server_socket.bind(('0.0.0.0', self.PORT))
        self.server_socket.listen(1)
        (client_socket, self.IP) = self.server_socket.accept()

        # listen on listen_socket for the server asking for identity
        word = client_socket.recv(1024)
        words = word.split(' ')
        check_words = words[1].split('~')

        if words[0] == 'GetIdentity':
            self.id = check_words[0]
            self.send_socket.sendall(self.identity)

        print '\nEstablished connection to', self.IP

        get_serial = GetSerial.GetSerial(self, self.id)
        get_serial.start()

    def send_string(self, string):
        new_string = self.id + ' ' + string + '~'
        self.send_socket.sendall('SendString~')
        self.send_socket.sendall(new_string)

    def close(self):
        print('Told server to close this socket')
        self.send_string('close~')
