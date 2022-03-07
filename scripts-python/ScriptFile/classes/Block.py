from bitstring import Bits
import os

class Block:
    def __init__(self,descriptor_ID,block_payload):
        #Start of header
        #Reserved bit
        self.descriptor_ID = descriptor_ID
        #Reserved bit
        self.block_payload_size = len(block_payload)
        #End of header
        self.block_payload = block_payload

    def write(self,path):
        with open(os.path.join(path,'block_hd'),'wb') as f:
            value = Bits(bin='0b0')+Bits(uint=self.descriptor_ID,length=7)+Bits(bin='0b0')+Bits(uint=self.block_payload_size,length=29)
            valueBytesBE = value.tobytes()
            valueBytesLE = int.from_bytes(valueBytesBE,byteorder='big').to_bytes(len(valueBytesBE),byteorder='little')
            f.write(valueBytesLE)
        with open(os.path.join(path,'block_payload'),'wb') as f:
            f.write(self.block_payload)