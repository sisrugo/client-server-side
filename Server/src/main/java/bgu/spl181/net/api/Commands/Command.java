package bgu.spl181.net.api.Commands;

import bgu.spl181.net.api.Protocol.UsersDatabase;

import java.io.Serializable;

public interface Command<T> extends Serializable {

    Serializable execute(T arg);

    void SetSharedUserData(UsersDatabase data);
}
