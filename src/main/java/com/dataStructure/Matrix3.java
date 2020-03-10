package com.dataStructure;

public class Matrix3 {
    public float data[] = new float[9];

    public Matrix3(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        init(x1, y1, z1, x2, y2, z2, x3, y3, z3);
    }

    public Matrix3() {
        init(1, 0, 0, 0, 1, 0, 0, 0, 1);
    }

    private void init(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3) {
        this.data[0] = x1; this.data[1] = y1; this.data[2] = z1;
        this.data[3] = x2; this.data[4] = y2; this.data[5] = z2;
        this.data[6] = x3; this.data[7] = y3; this.data[8] = z3;
    }

    public Vector3 times(Vector3 vector) {
        return new Vector3(
                vector.x * data[0] + vector.y * data[1] + vector.z * data[2],
                vector.x * data[3] + vector.y * data[4] + vector.z * data[5],
                vector.x * data[6] + vector.y * data[7] + vector.z * data[8]
        );
    }

    public Matrix3 times(Matrix3 mat) {
        return new Matrix3(
                data[0] * mat.data[0] + data[1] * mat.data[3] + data[2] * mat.data[6],
                data[0] * mat.data[1] + data[1] * mat.data[4] + data[2] * mat.data[7],
                data[0] * mat.data[2] + data[1] * mat.data[5] + data[2] * mat.data[8],

                data[3] * mat.data[0] + data[4] * mat.data[3] + data[5] * mat.data[6],
                data[3] * mat.data[1] + data[4] * mat.data[4] + data[5] * mat.data[7],
                data[3] * mat.data[2] + data[4] * mat.data[5] + data[5] * mat.data[8],

                data[6] * mat.data[0] + data[7] * mat.data[3] + data[8] * mat.data[6],
                data[6] * mat.data[1] + data[7] * mat.data[4] + data[8] * mat.data[7],
                data[6] * mat.data[2] + data[7] * mat.data[5] + data[8] * mat.data[8]
        );
    }

    public void timesEqual(Matrix3 mat) {
        float x1 = data[0] * mat.data[0] + data[1] * mat.data[3] + data[2] * mat.data[6],
              y1 = data[0] * mat.data[1] + data[1] * mat.data[4] + data[2] * mat.data[7],
              z1 = data[0] * mat.data[2] + data[1] * mat.data[5] + data[2] * mat.data[8],

              x2 = data[3] * mat.data[0] + data[4] * mat.data[3] + data[5] * mat.data[6],
              y2 = data[3] * mat.data[1] + data[4] * mat.data[4] + data[5] * mat.data[7],
              z2 = data[3] * mat.data[2] + data[4] * mat.data[5] + data[5] * mat.data[8],

              x3 = data[6] * mat.data[0] + data[7] * mat.data[3] + data[8] * mat.data[6],
              y3 = data[6] * mat.data[1] + data[7] * mat.data[4] + data[8] * mat.data[7],
              z3 = data[6] * mat.data[2] + data[7] * mat.data[5] + data[8] * mat.data[8];

        init(x1, y1, z1, x2, y2, z2, x3, y3, z3);
    }

    public Vector3 transform(Vector3 vector) {
        return this.times(vector);
    }

    public void setInverse(Matrix3 m) {
        float t1 = m.data[0] * m.data[4];
        float t2 = m.data[0] * m.data[5];
        float t3 = m.data[1] * m.data[3];
        float t4 = m.data[2] * m.data[3];
        float t5 = m.data[1] * m.data[6];
        float t6 = m.data[2] * m.data[6];

        // Calculate the determinant
        float det = (t1 * m.data[8] - t2 * m.data[8] - t3 * m.data[8] + t4 * m.data[7] + t5 * m.data[5] - t6 * m.data[4]);

        // Make sure the determinant is non-zero
        if (det == 0.0f) return;
        float invd = 1.0f/det;

        data[0] =  (m.data[4] * m.data[8] - m.data[5] * m.data[7]) * invd;
        data[1] = -(m.data[1] * m.data[8] - m.data[2] * m.data[7]) * invd;
        data[2] =  (m.data[1] * m.data[5] - m.data[2] * m.data[4]) * invd;
        data[3] = -(m.data[3] * m.data[8] - m.data[5] * m.data[6]) * invd;
        data[4] =  (m.data[0] * m.data[8] - t6                   ) * invd;
        data[5] = -(t2                    - t4                   ) * invd;
        data[6] =  (m.data[3] * m.data[7] - m.data[4] * m.data[6]) * invd;
        data[7] = -(m.data[0] * m.data[7] - t5                   ) * invd;
        data[8] =  (t1                    - t3                   ) * invd;
    }

    public Matrix3 inverse() {
        Matrix3 result = new Matrix3();
        result.setInverse(this);
        return result;
    }

    public void invert() {
        setInverse(this);
    }

    public void setTranspose(Matrix3 m) {
        data[0] = m.data[0];
        data[1] = m.data[3];
        data[2] = m.data[6];
        data[3] = m.data[1];
        data[4] = m.data[4];
        data[5] = m.data[7];
        data[6] = m.data[2];
        data[7] = m.data[5];
        data[8] = m.data[8];
    }

    public Matrix3 transpose() {
        Matrix3 result = new Matrix3();
        result.setTranspose(this);
        return result;
    }

    public void setOrientation(Quaternion q) {
        data[0] = 1 - (2*q.j*q.j + 2*q.k*q.k);
        data[1] = 2*q.i*q.j + 2*q.k*q.r;
        data[2] = 2*q.i*q.k - 2*q.j*q.r;
        data[3] = 2*q.i*q.j - 2*q.k*q.r;
        data[4] = 1 - (2*q.i*q.i  + 2*q.k*q.k);
        data[5] = 2*q.j*q.k + 2*q.i*q.r;
        data[6] = 2*q.i*q.k + 2*q.j*q.r;
        data[7] = 2*q.j*q.k - 2*q.i*q.r;
        data[8] = 1 - (2*q.i*q.i  + 2*q.j*q.j);
    }

    public Vector3 transformInverse(Vector3 vector) {
        return new Vector3(
                vector.x * data[0] +
                        vector.y * data[4] +
                        vector.z * data[8],

                vector.x * data[1] +
                        vector.y * data[5] +
                        vector.z * data[9],

                vector.x * data[2] +
                        vector.y * data[6] +
                        vector.z * data[10]
        );
    }

    public Vector3 transformDirection(Vector3 vector) {
        return new Vector3(
                vector.x * data[0] +
                        vector.y * data[1] +
                        vector.z * data[2],

                vector.x * data[4] +
                        vector.y * data[5] +
                        vector.z * data[6],

                vector.x * data[8] +
                        vector.y * data[9] +
                        vector.z * data[10]
        );
    }

    public Vector3 transformInverseDirection(Vector3 vector) {
        return new Vector3(
                vector.x * data[0] +
                        vector.y * data[4] +
                        vector.z * data[8],

                vector.x * data[1] +
                        vector.y * data[5] +
                        vector.z * data[9],

                vector.x * data[2] +
                        vector.y * data[6] +
                        vector.z * data[10]
        );
    }
}
