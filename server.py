import socket
import threading
import time
import os
import uuid

class Server:
    def __init__(self, host, port):
        self.SERVER = host
        self.PORT = port
        self.ADDR = (self.SERVER, self.PORT)
        self.FORMAT = 'utf-8'
        self.HEADER = 64
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server.bind(self.ADDR)
        self.DISCONNECT_MESSAGE = "!DISCONNECT"
        self.SIZE = 1024 * 2
        self.count = 0
        self.String_count = 0

    def handle_client(self, conn, addr):
        connected = True
        try:
            while connected:
                if self.find_string(self.String_count):
                    self.start_timer()
                else:
                    print('String not found. Exiting.')
                    break
        except ConnectionResetError:
            print(f"Connection with {addr} was forcibly closed by the client.")
        finally:
            conn.close()
            print(f"Connection with {addr} closed.")

    def start_server(self):
        print("Starting server...")
        self.server.listen()
        print(f"Server is listening on {self.SERVER}")
        while True:
            conn, addr = self.server.accept()
            mac_address = self.get_mac_address()
            self.send_mac_address(conn, mac_address)
            thread = threading.Thread(target=self.handle_client, args=(conn, addr))
            thread.start()
            print(f"[ACTIVE CONNECTIONS]: {threading.active_count() - 1}")

    def find_string(self, string_count):
        '''
        This function helps to check log file to run timer script 
        '''
        search_string = b"please enter a string"
        search_string2 = b"please enter a string"
        file_path = "please enter a file path"
        file_path1 = "please enter a file path"
        attempt = 0
        while attempt < 10:
            try:
                with open(file_path, 'rb') as file:
                    content = file.read()
                    if search_string in content:
                        print("String found in path:", file_path, "and waiting for 10 sec")
                        try:
                            with open(file_path1, 'rb') as file1:
                                content1 = file1.read()
                                print(f"Content read from {file_path1}: {content1}")
                                if search_string2 in content1:
                                    print(f"String found in path {file_path1}, string == {search_string2}")
                                    return True
                                else:
                                    print(f"String not found in path {file_path1}")
                                    time.sleep(3)
                                    return False
                        except FileNotFoundError:
                            print(f"File not found: {file_path1}")
                            attempt+=1
                            if attempt == 9:
                                print("Maximum attempts reached. Returning True.")
                                return True
                            else:
                                self.String_count += 10  # Incrementing the count by 10
                    else:
                        # Wait for 1 second before retrying
                        return False
            except FileNotFoundError:
                print(f"File not found: {file_path}")
                time.sleep(1)  # Wait for 1 second before retrying
                attempt += 1
        print("String not found after multiple attempts.")

    def start_timer(self):
        local_time = time.localtime()
        hour = local_time.tm_hour
        minute = local_time.tm_min
        self.minutes_function(minute, hour)

    def minutes_function(self, minute, hour):
        count_hour = 0
        while True:
            if hour == 24:
                self.shutdown_pc()
            for i in range(minute, 59):
                time.sleep(60)
                minute += 1
                print(f"{hour}:{minute}")
                if minute == 59:
                    hour += 1
                    count_hour += 1
                    if count_hour == 1:
                        print("Inside count hour.")
                        thread = threading.Thread(target=self.find_string, args=(self.String_count,))
                        thread.start()
                        if self.find_string(self.count):
                            self.minutes_function(minute=0, hour=hour)
                        else:
                            print("String not found.")
                    minute = 0

    def send_mac_address(self, conn, mac_address):
        message = mac_address.encode(self.FORMAT)
        msg_length = len(message)
        send_length = str(msg_length).encode(self.FORMAT)
        send_length += b' ' * (self.HEADER - len(send_length))
        conn.send(send_length)
        conn.send(message)

    def shutdown_pc(self):
        os.system('shutdown /s /t 1')

    def get_mac_address(self) -> str:
        mac = ':'.join(['{:02x}'.format((uuid.getnode() >> elements) & 0xff) for elements in range(0, 2 * 6, 2)])
        return mac

if __name__ == "__main__":
    server_instance = Server(host=socket.gethostbyname(socket.gethostname()), port=1050)
    server_instance.start_server()
