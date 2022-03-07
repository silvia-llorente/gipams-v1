import os

class KLV:
    def __init__(self,key,value):
        self.key = key
        self.length = len(value).to_bytes(8,byteorder='little')
        self.value = value
        if len(self.key) != 4:
            raise ValueError('Invalid key')

    def write(self,path):
        with open(os.path.join(path,self.key),'wb') as f:
            f.write(bytes(self.key,'utf-8')+self.length+self.value)