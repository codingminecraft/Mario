package com.dataStructure;

public class Quaternion {
    public float r, i, j, k;

    public Quaternion() {
        r = i = j = k = 0.0f;
    }

    public Quaternion(float r, float i, float j, float k) {
        this.r = r; this.i = i; this.j = j; this.k = k;
    }

    public void normalize() {
        float d = r * r + i * i + j * j + k * k;

        if (d == 0) {
            r = 1;
            return;
        }

        d = (float)(1.0/Math.sqrt(d));
        r *= d;
        i *= d;
        j *= d;
        k *= d;
    }

    public void timesEqual(Quaternion multiplier) {
        Quaternion q = this;
        r = q.r * multiplier.r - q.i * multiplier.i - q.j * multiplier.j - q.k * multiplier.k;
        i = q.r * multiplier.i + q.i * multiplier.r + q.j * multiplier.k - q.k * multiplier.j;
        j = q.r * multiplier.j + q.j * multiplier.r + q.k * multiplier.i - q.i * multiplier.k;
        k = q.r * multiplier.k + q.k * multiplier.r + q.i * multiplier.j - q.j * multiplier.i;
    }

    public void rotateByVector(Vector3 vector) {
        Quaternion q = new Quaternion(0, vector.x, vector.y, vector.z);
        this.timesEqual(q);
    }

    public void addScaledVector(Vector3 vector, float scale) {
        Quaternion q = new Quaternion(0, vector.x * scale, vector.y * scale, vector.z * scale);
        q.timesEqual(this);
        r += q.r * 0.5f;
        i += q.i * 0.5f;
        j += q.j * 0.5f;
        k += q.k * 0.5f;
    }
}
