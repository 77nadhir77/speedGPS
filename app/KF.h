//
// Created by DELL on 4/17/2024.
//

#ifndef KALMANFILTERSENSORFUSION_KF_H
#define KALMANFILTERSENSORFUSION_KF_H

#include <vector>


class KF {
public:
    static const int NUM_VARS = 2;
    static const int iX = 0;
    static const int iV = 1;

    using Vector = std::vector<double>;
    using Matrix = std::vector<std::vector<double>>;;



};


#endif //KALMANFILTERSENSORFUSION_KF_H
