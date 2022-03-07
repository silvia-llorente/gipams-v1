from bitstring import Bits
from classes.KLV import KLV

class AccessUnitHeader:
    def __init__(self,access_unit_ID,parameter_set_ID,AU_type,reads_count):
        self.access_unit_ID = access_unit_ID
        self.num_blocks = 0
        self.parameter_set_ID = parameter_set_ID
        self.AU_type = AU_type
        self.reads_count = reads_count

    def write(self,path):
        value = self.access_unit_ID.to_bytes(4,byteorder='little')+self.num_blocks.to_bytes(1,byteorder='little')+self.parameter_set_ID.to_bytes(1,byteorder='little')
        AU_type_bits = Bits(uint=self.AU_type,length=4)
        reads_count_bits = Bits(uint=self.reads_count,length=32)
        valueBits = AU_type_bits+reads_count_bits
        valueBytesBE = valueBits.tobytes()
        valueBytesLE = int.from_bytes(valueBytesBE,byteorder='big').to_bytes(len(valueBytesBE),byteorder='little')
        value = value + valueBytesLE
        klv = KLV('auhd',value)
        klv.write(path)