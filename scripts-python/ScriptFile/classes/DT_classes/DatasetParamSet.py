from classes.KLV import KLV
from bitstring import Bits

class DatasetParamSet:
    def __init__(self,parameter_set_ID,parent_parameter_set_ID,multiple_alignment_flag,pos_40_bits_flag,alphabet_ID):
        self.dataset_group_ID = None
        self.dataset_ID = None
        self.parameter_set_ID = parameter_set_ID
        self.parent_parameter_set_ID = parent_parameter_set_ID
        self.multiple_alignment_flag = multiple_alignment_flag
        self.pos_40_bits_flag = pos_40_bits_flag
        self.alphabet_ID = alphabet_ID
    
    def updateIds(self,dataset_group_ID,dataset_ID):
        self.dataset_group_ID = dataset_group_ID
        self.dataset_ID = dataset_ID
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')
        elif self.dataset_ID.bit_length() > 16:
            raise ValueError('Invalid dataset_ID')
    
    def write(self,path):
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.dataset_ID.to_bytes(2,byteorder='little')+self.parameter_set_ID.to_bytes(1,byteorder='little')+self.parent_parameter_set_ID.to_bytes(1,byteorder='little')
        multiple_alignment_flag_bits = Bits(bin=self.multiple_alignment_flag)
        pos_40_bits_flag_bits = Bits(bin=self.multiple_alignment_flag)    
        alphabet_ID_bits = Bits(uint=self.alphabet_ID,length=8)
        valueBits = multiple_alignment_flag_bits+pos_40_bits_flag_bits+alphabet_ID_bits
        valueBytesBE = valueBits.tobytes()
        valueBytesLE = int.from_bytes(valueBytesBE,byteorder='big').to_bytes(len(valueBytesBE),byteorder='little')
        value = value+valueBytesLE
        klv = KLV('pars',value)
        klv.write(path)