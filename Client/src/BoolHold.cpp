//
// Created by shakedah on 1/11/18.
//

#include "../include/BoolHold.h"



BoolHold::BoolHold () : shouldTerm(false){}

BoolHold::~BoolHold(){

}

void BoolHold::setBool(bool res){
    shouldTerm = res;
}

bool BoolHold::getBool(){
    return shouldTerm;
}