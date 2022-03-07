from classes.KLV import KLV
from bitstring import Bits

class DatasetHeader:
    def __init__(self,version,dataset_type,alphabet_ID):
        self.dataset_group_ID = None
        self.dataset_ID = None
        self.version = version
        self.flags1 = '0b0000111'
        self.seq_count = 0
        #0, 1 o 2
        self.dataset_type = dataset_type
        #self.num_classes = 0
        #self.clid = []
        #self.num_descriptors = []
        #self.descriptor_ID = [][]
        self.parameters_update_flag = '0b0'
        #0 o 1
        self.alphabet_ID = alphabet_ID
        self.num_U_access_units = 0
        #self.reserved = None
        #self.U_signature_flag = U_signature_flag
        #self.U_signature_constant_length = U_signature_constant_length
        #self.U_signature_length = U_signature_length
        #self.reserved_flag = None
        #self.reservedF = None        
        #self.reserved_flag2 = None
        #self.tflag = [1]
        #self.thres = [thresInit]
        #Alineació a byte
        
        if self.dataset_type < 0 or self.dataset_type > 2:
            raise ValueError('Invalid dataset type')
        elif self.alphabet_ID != 0 and self.dataset_type != 1:
            raise ValueError('Invalid alphabet id')

    def updateIds(self,dataset_group_ID,dataset_ID):
        self.dataset_group_ID = dataset_group_ID
        self.dataset_ID = dataset_ID
        if self.dataset_group_ID.bit_length() > 8:
            raise ValueError('Invalid dataset_group_ID')
        elif self.dataset_ID.bit_length() > 16:
            raise ValueError('Invalid dataset_ID')

    def write(self,path):
        #Primers 7 bytes
        value = self.dataset_group_ID.to_bytes(1,byteorder='little')+self.dataset_ID.to_bytes(2,byteorder='little')+bytes(self.version,'UTF-8')
        flags1_bits = Bits(bin=self.flags1)
        seq_count_bits = Bits(uint=self.seq_count,length=16)
        dataset_type_bits = Bits(uint=self.dataset_type,length=4)
        parameters_update_flag_bits = Bits(bin=self.parameters_update_flag)
        alphabet_ID_bits = Bits(uint=self.alphabet_ID,length=7)
        num_U_access_units_bits = Bits(uint=self.num_U_access_units,length=32)
        valueBits = flags1_bits+seq_count_bits+dataset_type_bits+parameters_update_flag_bits+alphabet_ID_bits+num_U_access_units_bits+(Bits(bin='0b0'))
        #Funcio utilitza big endian, fa padding a byte
        valueBytesBE = valueBits.tobytes()
        #Convertir a little endian
        valueBytesLE = int.from_bytes(valueBytesBE,byteorder='big').to_bytes(len(valueBytesBE),byteorder='little')
        value = value+valueBytesLE
        klv = KLV('dthd',value)
        klv.write(path)
