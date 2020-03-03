package com.dataStructure;

public class Matrix4 {
    public float data[] = new float[12];

    // We are actually multiplying by a 4x4 matrix here with the
    // 4th row set to [0 0 0 1], thus we just add it on to the end
    // of the result
    public Vector3 times(Vector3 vector) {
        return new Vector3(
                vector.x * data[0] + vector.y * data[1] + vector.z * data[2] + data[3],
                vector.x * data[4] + vector.y * data[5] + vector.z * data[6] + data[7],
                vector.x * data[8] + vector.y * data[9] + vector.z * data[10] + data[11]
        );
    }

    public Vector3 transform(Vector3 vector) {
        return this.times(vector);
    }

    public Matrix4 times(Matrix4 mat) {
        Matrix4 result = new Matrix4();
        result.data[0]  = mat.data[0] * this.data[0] + mat.data[4] * this.data[1] + mat.data[8] * this.data[2];
        result.data[4]  = mat.data[0] * this.data[4] + mat.data[4] * this.data[5] + mat.data[8] * this.data[6];
        result.data[8]  = mat.data[0] * this.data[8] + mat.data[4] * this.data[9] + mat.data[8] * this.data[10];

        result.data[1]  = mat.data[1] * this.data[0] + mat.data[5] * this.data[1] + mat.data[9] * this.data[2];
        result.data[5]  = mat.data[1] * this.data[4] + mat.data[5] * this.data[5] + mat.data[9] * this.data[6];
        result.data[9]  = mat.data[1] * this.data[8] + mat.data[5] * this.data[9] + mat.data[9] * this.data[10];

        result.data[2]  = mat.data[2] * this.data[0] + mat.data[6] * this.data[1] + mat.data[10] * this.data[2];
        result.data[6]  = mat.data[2] * this.data[4] + mat.data[6] * this.data[5] + mat.data[10] * this.data[6];
        result.data[10] = mat.data[2] * this.data[8] + mat.data[6] * this.data[9] + mat.data[10] * this.data[10];

        result.data[3]  = mat.data[3] * this.data[0] + mat.data[7] * this.data[1] + mat.data[11] * this.data[2] + this.data[3];
        result.data[7]  = mat.data[3] * this.data[4] + mat.data[7] * this.data[5] + mat.data[11] * this.data[6] + this.data[7];
        result.data[11] = mat.data[3] * this.data[8] + mat.data[7] * this.data[9] + mat.data[11] * this.data[10] + this.data[11];

        return result;
    }

    public Matrix4 inverse() {
        Matrix4 result = new Matrix4();
        result.setInverse(this);
        return result;
    }

    public void invert() {
        setInverse(this);
    }

    public float getDeterminant() {
        return -data[8]*data[5]*data[2]+
                data[4]*data[9]*data[2]+
                data[8]*data[1]*data[6]-
                data[0]*data[9]*data[6]-
                data[4]*data[1]*data[10]+
                data[0]*data[5]*data[10];
    }

    public void setInverse(Matrix4 m) {
        // Make sure the determinant is non-zero.
        float det = getDeterminant();
        if (det == 0) return;
        det = 1.0f/det;

        data[0] = (-m.data[9]*m.data[6]+m.data[5]*m.data[10])*det;
        data[4] = (m.data[8]*m.data[6]-m.data[4]*m.data[10])*det;
        data[8] = (-m.data[8]*m.data[5]+m.data[4]*m.data[9])*det;

        data[1] = (m.data[9]*m.data[2]-m.data[1]*m.data[10])*det;
        data[5] = (-m.data[8]*m.data[2]+m.data[0]*m.data[10])*det;
        data[9] = (m.data[8]*m.data[1]-m.data[0]*m.data[9])*det;

        data[2] = (-m.data[5]*m.data[2]+m.data[1]*m.data[6])*det;
        data[6] = (+m.data[4]*m.data[2]-m.data[0]*m.data[6])*det;
        data[10] = (-m.data[4]*m.data[1]+m.data[0]*m.data[5])*det;

        data[3] = (m.data[9]*m.data[6]*m.data[3]
                -m.data[5]*m.data[10]*m.data[3]
                -m.data[9]*m.data[2]*m.data[7]
                +m.data[1]*m.data[10]*m.data[7]
                +m.data[5]*m.data[2]*m.data[11]
                -m.data[1]*m.data[6]*m.data[11])*det;
        data[7] = (-m.data[8]*m.data[6]*m.data[3]
                +m.data[4]*m.data[10]*m.data[3]
                +m.data[8]*m.data[2]*m.data[7]
                -m.data[0]*m.data[10]*m.data[7]
                -m.data[4]*m.data[2]*m.data[11]
                +m.data[0]*m.data[6]*m.data[11])*det;
        data[11] =(m.data[8]*m.data[5]*m.data[3]
                -m.data[4]*m.data[9]*m.data[3]
                -m.data[8]*m.data[1]*m.data[7]
                +m.data[0]*m.data[9]*m.data[7]
                +m.data[4]*m.data[1]*m.data[11]
                -m.data[0]*m.data[5]*m.data[11])*det;
    }

    public void setOrientation(Quaternion q, Vector3 pos) {
        data[0] = 1 - (2*q.j*q.j + 2*q.k*q.k);
        data[1] = 2*q.i*q.j + 2*q.k*q.r;
        data[2] = 2*q.i*q.k - 2*q.j*q.r;
        data[3] = pos.x;

        data[4] = 2*q.i*q.j - 2*q.k*q.r;
        data[5] = 1 - (2*q.i*q.i  + 2*q.k*q.k);
        data[6] = 2*q.j*q.k + 2*q.i*q.r;
        data[7] = pos.y;

        data[8] = 2*q.i*q.k + 2*q.j*q.r;
        data[9] = 2*q.j*q.k - 2*q.i*q.r;
        data[10] = 1 - (2*q.i*q.i  + 2*q.j*q.j);
        data[11] = pos.z;
    }
}
