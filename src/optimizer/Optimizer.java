package optimizer;

import tensor.Tensor;

import java.util.List;

public class Optimizer implements IOptimizer {
    private List<Tensor> parameters;
    private float learningRate;

    public Optimizer(List<Tensor> parameters, float learningRate) {
        if(learningRate <= 0.0f) {
            learningRate = 1E-4f;
        }
        this.parameters = parameters;
        this.learningRate = learningRate;
    }

    @Override
    public void step() {
        for(Tensor p: parameters) {
            p.update(-this.learningRate);
        }
    }

    @Override
    public List<Tensor> getParameters() {
        return this.parameters();
    }

    @Override
    public List<Tensor> parameters() {
        return this.parameters;
    }

    @Override
    public void print() {
        for(Tensor p: parameters) {
            p.print();
        }
    }

    @Override
    public void zeroGrad() {
        // Reset all gradients
        for(Tensor parameter: parameters) {
            parameter.gradient.zero_();
        }
    }
}
