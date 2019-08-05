//
// Created by shakedah on 1/11/18.
//

#ifndef SPL3_CLIENT_BOOLHOLD_H
#define SPL3_CLIENT_BOOLHOLD_H


class BoolHold {
    private:
        bool shouldTerm;

    public:
        BoolHold();

        ~BoolHold();

        void setBool(bool res);

        bool getBool();
};


#endif //SPL3_CLIENT_BOOLHOLD_H
