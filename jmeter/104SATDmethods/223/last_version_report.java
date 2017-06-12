	public Sampler next() {
        // Check if transaction is done
        if(transactionSampler != null && transactionSampler.isTransactionDone()) {
            log.debug("End of transaction");
            // This transaction is done
            transactionSampler = null;
            return null;
        }
        
        // Check if it is the start of a new transaction
		if (isFirst()) // must be the start of the subtree
		{
		    log.debug("Start of transaction");
		    transactionSampler = new TransactionSampler(this, getName());
		}

        // Sample the children of the transaction
		Sampler subSampler = super.next();
        transactionSampler.setSubSampler(subSampler);
        // If we do not get any sub samplers, the transaction is done
        if (subSampler == null) {
            transactionSampler.setTransactionDone();
        }
        return transactionSampler;
	}
