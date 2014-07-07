/**
 * 
 * Copyright (C) 2014 Seagate Technology.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.seagate.kinetic.simulator.client.async;

import java.util.concurrent.LinkedBlockingQueue;

import kinetic.client.AsyncKineticException;
import kinetic.client.CallbackHandler;
import kinetic.client.CallbackResult;
import kinetic.client.Entry;

import com.seagate.kinetic.common.lib.KineticMessage;
import com.seagate.kinetic.proto.Kinetic.Message.Status.StatusCode;

public class MyCallBack implements CallbackHandler<Entry> {

	private LinkedBlockingQueue<KineticMessage> pbq = null;

	private KineticMessage request = null;

	public MyCallBack(LinkedBlockingQueue<KineticMessage> pbq, KineticMessage m) {
		this.pbq = pbq;
		request = m;
	}

	public void onMessage(KineticMessage message) {

		if (message.getMessage().getCommand().getStatus().getCode() != StatusCode.SUCCESS) {
			throw new RuntimeException("unsuccessful status, message="
					+ message.getMessage().getCommand().getStatus()
							.getStatusMessage() + ", code="
					+ message.getMessage().getCommand().getStatus().getCode());
		}

		if (this.request.getMessage().getCommand().getHeader().getSequence() != message
				.getMessage().getCommand()
				.getHeader().getAckSequence()) {
			throw new RuntimeException("call back sequence error");
		}

		this.pbq.add(message);
		// System.out.println("received message: " + message);
	}

	@Override
	public void onSuccess(CallbackResult<Entry> result) {

		// System.out.println("received async callback, result="
		// + result.getResult().getClass().getName());
		this.pbq.add(result.getResponseMessage());
	}

	@Override
	public void onError(AsyncKineticException exception) {
		// TODO Auto-generated method stub

	}

}